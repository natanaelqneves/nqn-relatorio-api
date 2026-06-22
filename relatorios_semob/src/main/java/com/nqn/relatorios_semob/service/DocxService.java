package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.model.Relatorio;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
public class DocxService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public byte[] gerarDocx(Relatorio relatorio) {
        try {
            InputStream templateStream = new ClassPathResource("templates/template_relatorio.docx").getInputStream();
            XWPFDocument documento = new XWPFDocument(templateStream);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Map<String, String> tags = new HashMap<>();

            // Dados de Texto Normais
            tags.put("{{nomeCompleto}}", relatorio.getUsuario().getNomeCompleto() != null ? relatorio.getUsuario().getNomeCompleto() : "");
            tags.put("{{matricula}}", relatorio.getUsuario().getMatricula() != null ? relatorio.getUsuario().getMatricula() : "");
            tags.put("{{descricaoServico}}", relatorio.getDescricaoServico());
            tags.put("{{placaViatura}}", relatorio.getViatura().getPlaca());
            tags.put("{{modelo}}", relatorio.getViatura().getModelo());
            tags.put("{{ano}}", relatorio.getViatura().getAno());
            tags.put("{{horaInicio}}", relatorio.getHoraIncio().toString());
            tags.put("{{horaFim}}", relatorio.getHoraFinal().toString());
            tags.put("{{dataDoServico}}", relatorio.getDataDoServico().format(formatter));

            String diaDaSemanaCapitalizado = relatorio.getDataDoServico().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            tags.put("{{diaDaSemana}}", diaDaSemanaCapitalizado.substring(0, 1).toUpperCase() + diaDaSemanaCapitalizado.substring(1));
            tags.put("{{kmInicial}}", String.valueOf(relatorio.getKmInicial()));
            tags.put("{{kmFinal}}", String.valueOf(relatorio.getKmFinal()));
            tags.put("{{avarias}}", relatorio.getAvarias() != null ? relatorio.getAvarias() : "Nenhuma");
            tags.put("{{sinistro}}", relatorio.getSinistro() != null ? relatorio.getSinistro() : "Nenhum");

            // =======================================================================
            // 🛡️ REGULAGEM DAS OCORRÊNCIAS: Evita o estouro caso a lista venha vazia
            // =======================================================================
            boolean temOcorrencia = relatorio.getOcorrencias() != null && !relatorio.getOcorrencias().isEmpty();
            String urlFotoSupabase = null;

            if (temOcorrencia) {
                tags.put("{{descricaoOcorrencia}}", relatorio.getOcorrencias().get(0).getDescricaoDefeito() != null ?
                        relatorio.getOcorrencias().get(0).getDescricaoDefeito() : "Nenhuma descrição informada.");
                urlFotoSupabase = relatorio.getOcorrencias().get(0).getUrlFoto();
            } else {
                // Valores padrão para relatórios sem alteração técnica / sem ocorrência
                tags.put("{{descricaoOcorrencia}}", "Sem alterações / Nenhuma ocorrência registrada durante o plantão.");
            }

            String urlFotoAssinatura = relatorio.getUsuario().getAssinatura();

            System.out.println("Foto Ocorrência: " + urlFotoSupabase);
            tags.put("{{fotoOcorrencia}}", urlFotoSupabase != null ? urlFotoSupabase : "");
            tags.put("{{assinatura}}", urlFotoAssinatura != null ? urlFotoAssinatura : "");
            // =======================================================================

            // 3. Processa parágrafos normais e tabelas
            substituirTagsEImagens(documento.getParagraphs(), tags);

            for (XWPFTable tabela : documento.getTables()) {
                for (XWPFTableRow linha : tabela.getRows()) {
                    for (XWPFTableCell celula : linha.getTableCells()) {
                        substituirTagsEImagens(celula.getParagraphs(), tags);
                    }
                }
            }

            ByteArrayOutputStream wordOutputStream = new ByteArrayOutputStream();
            documento.write(wordOutputStream);
            documento.close();

            return wordOutputStream.toByteArray();

        } catch (Exception e) {
            // Log detalhado no console do Render para você rastrear se algo mais falhar
            e.printStackTrace();
            throw new RuntimeException("Erro catastrófico ao preencher e gerar o documento do relatório. Causa: " + e.getMessage(), e);
        }
    }

    private void substituirTagsEImagens(List<XWPFParagraph> paragrafos, Map<String, String> tags) {
        for (XWPFParagraph paragrafo : paragrafos) {
            String textoParagrafo = paragrafo.getParagraphText();

            if (textoParagrafo != null && textoParagrafo.contains("{{")) {

                // 🚨 1. CASO ESPECÍFICO: É a tag da imagem da câmera?
                if (textoParagrafo.contains("{{fotoOcorrencia}}")) {
                    String urlDaImagem = tags.get("{{fotoOcorrencia}}");

                    // Limpa o texto da tag antiga removendo os pedaços (runs) do Word
                    limparRunsDoParagrafo(paragrafo);
                    XWPFRun novoRun = paragrafo.createRun();

                    if (urlDaImagem != null && !urlDaImagem.isBlank()) {
                        try {
                            String urlFormatada = urlDaImagem.trim().replace(" ", "%20");

                            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlFormatada)).build();
                            byte[] imagemBytes = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray()).body();

                            if (imagemBytes != null && imagemBytes.length > 0) {

                                // 1. Detecta o tipo do arquivo (PNG ou JPEG)
                                int tipoImagemPOI = Document.PICTURE_TYPE_JPEG;
                                String extensao = "jpg";

                                if (imagemBytes.length > 4 &&
                                        imagemBytes[0] == (byte)0x89 && imagemBytes[1] == (byte)0x50 &&
                                        imagemBytes[2] == (byte)0x4E && imagemBytes[3] == (byte)0x47) {
                                    tipoImagemPOI = Document.PICTURE_TYPE_PNG;
                                    extensao = "png";
                                }

                                // =======================================================================
                                // 🚨 NOVA LÓGICA: LÊ AS DIMENSÕES REAIS DO FRONT-END (ANTI-DISTORÇÃO)
                                // =======================================================================
                                int larguraRealPixels;
                                int alturaRealPixels;

                                try (InputStream dimStream = new java.io.ByteArrayInputStream(imagemBytes)) {
                                    java.awt.image.BufferedImage imagemAgente = javax.imageio.ImageIO.read(dimStream);
                                    if (imagemAgente != null) {
                                        larguraRealPixels = imagemAgente.getWidth();
                                        alturaRealPixels = imagemAgente.getHeight();

                                    } else {
                                        // Fallback de segurança caso o ImageIO falhe em ler o cabeçalho
                                        larguraRealPixels = 300;
                                        alturaRealPixels = 400;
                                    }
                                }

                                // Opcional: Se a imagem do front ainda for muito grande para a folha A4 (ex: mais que 450px de largura),
                                // podemos aplicar uma escala simples para ela caber na margem do Word sem esticar:
                                double escala = 1.0;
                                if (larguraRealPixels > 450) {
                                    escala = 450.0 / larguraRealPixels; // Reduz o tamanho mantendo a proporção matemática
                                }

                                int larguraFinalEmPontos = (int) (larguraRealPixels * escala);
                                int alturaFinalEmPontos = (int) (alturaRealPixels * escala);
                                // =======================================================================

                                try (InputStream imageStream = new java.io.ByteArrayInputStream(imagemBytes)) {
                                    novoRun.addCarriageReturn();

                                    // 🚨 AGORA USA AS MEDIDAS PROPORCIONAIS CALCULADAS DINAMICAMENTE
                                    novoRun.addPicture(
                                            imageStream,
                                            tipoImagemPOI,
                                            "foto_ocorrencia." + extensao,
                                            Units.toEMU(larguraFinalEmPontos), // Largura real ou escalada
                                            Units.toEMU(alturaFinalEmPontos)  // Altura proporcional perfeita
                                    );

                                    novoRun.addCarriageReturn();
                                }
                            } else {
                                novoRun.setText("[O arquivo de imagem retornou vazio do servidor]");
                            }
                        } catch (Exception e) {
                            System.err.println("💥 Erro ao processar proporção da imagem: " + e.getMessage());
                            novoRun.setText("[Erro ao processar imagem remota: " + e.getMessage() + "]");
                        }
                    } else {
                        novoRun.setText("Nenhuma foto anexada a esta ocorrência.");
                    }
                    continue; // Avança para o próximo parágrafo
                }

                // 🚨 2. FLUXO NORMAL: Substituição de textos (Incluso descrição da ocorrência)
                boolean mudou = false;
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    // Não deixa o loop de texto misturar com a lógica da foto
                    if (!tag.getKey().equals("{{fotoOcorrencia}}") && textoParagrafo.contains(tag.getKey())) {
                        textoParagrafo = textoParagrafo.replace(tag.getKey(), tag.getValue());
                        mudou = true;
                    }
                }

                if (mudou) {
                    limparRunsDoParagrafo(paragrafo);
                    XWPFRun novoRun = paragrafo.createRun();
                    novoRun.setText(textoParagrafo);
                }
            }
        }
    }

    private void limparRunsDoParagrafo(XWPFParagraph paragrafo) {
        int totalRuns = paragrafo.getRuns().size();
        for (int i = totalRuns - 1; i >= 0; i--) {
            paragrafo.removeRun(i);
        }
    }
}