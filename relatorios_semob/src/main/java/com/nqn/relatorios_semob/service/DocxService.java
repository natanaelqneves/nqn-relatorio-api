package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.model.Relatorio;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

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

            String diaDaSemana = relatorio.getDataDoServico().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            tags.put("{{diaDaSemana}}", diaDaSemana.substring(0, 1).toUpperCase() + diaDaSemana.substring(1));

            tags.put("{{kmInicial}}", String.valueOf(relatorio.getKmInicial()));
            tags.put("{{kmFinal}}", String.valueOf(relatorio.getKmFinal()));
            tags.put("{{avarias}}", relatorio.getAvarias() != null ? relatorio.getAvarias() : "Nenhuma");
            tags.put("{{sinistro}}", relatorio.getSinistro() != null ? relatorio.getSinistro() : "Nenhum");


            boolean temOcorrencia = relatorio.getOcorrencias() != null && !relatorio.getOcorrencias().isEmpty();
            String urlFotoSupabase = null;

            if (temOcorrencia) {
                tags.put("{{descricaoOcorrencia}}", relatorio.getOcorrencias().get(0).getDescricaoDefeito() != null ?
                        relatorio.getOcorrencias().get(0).getDescricaoDefeito() : "Nenhuma descrição informada.");
                urlFotoSupabase = relatorio.getOcorrencias().get(0).getUrlFoto();
            } else {
                tags.put("{{descricaoOcorrencia}}", "Sem alterações / Nenhuma ocorrência registrada durante o plantão.");
            }

            String urlFotoAssinatura = relatorio.getUsuario().getAssinatura();

            tags.put("{{fotoOcorrencia}}", urlFotoSupabase != null ? urlFotoSupabase : "");
            tags.put("{{assinatura}}", urlFotoAssinatura != null ? urlFotoAssinatura : "");

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
            e.printStackTrace();
            throw new RuntimeException("Erro ao preencher e gerar o documento do relatório. Causa: " + e.getMessage(), e);
        }
    }

    private void substituirTagsEImagens(List<XWPFParagraph> paragrafos, Map<String, String> tags) {
        for (XWPFParagraph paragrafo : paragrafos) {
            String textoParagrafo = paragrafo.getParagraphText();

            if (textoParagrafo != null && textoParagrafo.contains("{{")) {

                if (textoParagrafo.contains("{{fotoOcorrencia}}")) {
                    String urlDaImagem = tags.get("{{fotoOcorrencia}}");
                    processarEInserirImagemRemota(paragrafo, urlDaImagem, "foto_ocorrencia", 400.0);
                    continue;
                }

                if (textoParagrafo.contains("{{assinatura}}")) {
                    String urlDaAssinatura = tags.get("{{assinatura}}");
                    // Definimos uma largura máxima menor para a assinatura (ex: 200.0) para ficar esteticamente elegante no documento
                    processarEInserirImagemRemota(paragrafo, urlDaAssinatura, "assinatura_agente", 200.0);
                    continue;
                }

                boolean mudou = false;
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    // Impede que as tags de mídia entrem no fluxo de substituição de texto comum
                    if (!tag.getKey().equals("{{fotoOcorrencia}}") &&
                            !tag.getKey().equals("{{assinatura}}") &&
                            textoParagrafo.contains(tag.getKey())) {

                        textoParagrafo = textoParagrafo.replace(tag.getKey(), tag.getValue() != null ? tag.getValue() : "");
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
    private void processarEInserirImagemRemota(XWPFParagraph paragrafo, String urlDaImagem, String nomeArquivoBase, double larguraMaximaDesejada) {
        limparRunsDoParagrafo(paragrafo);
        XWPFRun novoRun = paragrafo.createRun();

        if (urlDaImagem != null && !urlDaImagem.isBlank()) {
            try {
                String urlFormatada = urlDaImagem.trim().replace(" ", "%20");

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(urlFormatada))
                        .build();

                byte[] imagemBytes = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).body();

                if (imagemBytes != null && imagemBytes.length > 0) {

                    int tipoImagemPOI = org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_JPEG;
                    String extensao = "jpg";

                    if (imagemBytes.length > 4 &&
                            imagemBytes[0] == (byte) 0x89 && imagemBytes[1] == (byte) 0x50 &&
                            imagemBytes[2] == (byte) 0x4E && imagemBytes[3] == (byte) 0x47) {
                        tipoImagemPOI = org.apache.poi.xwpf.usermodel.Document.PICTURE_TYPE_PNG;
                        extensao = "png";
                    }

                    int larguraRealPixels;
                    int alturaRealPixels;

                    try (java.io.InputStream dimStream = new java.io.ByteArrayInputStream(imagemBytes)) {
                        java.awt.image.BufferedImage imagemAgente = javax.imageio.ImageIO.read(dimStream);
                        if (imagemAgente != null) {
                            larguraRealPixels = imagemAgente.getWidth();
                            alturaRealPixels = imagemAgente.getHeight();
                        } else {
                            larguraRealPixels = 300;
                            alturaRealPixels = 150;
                        }
                    }

                    double escala = 1.0;
                    if (larguraRealPixels > larguraMaximaDesejada) {
                        escala = larguraMaximaDesejada / larguraRealPixels;
                    }

                    int larguraFinalEmPontos = (int) (larguraRealPixels * escala);
                    int alturaFinalEmPontos = (int) (alturaRealPixels * escala);

                    try (java.io.InputStream imageStream = new java.io.ByteArrayInputStream(imagemBytes)) {
                        novoRun.addCarriageReturn(); // Quebra de linha antes

                        novoRun.addPicture(
                                imageStream,
                                tipoImagemPOI,
                                nomeArquivoBase + "." + extensao,
                                org.apache.poi.util.Units.toEMU(larguraFinalEmPontos),
                                org.apache.poi.util.Units.toEMU(alturaFinalEmPontos)
                        );

                        novoRun.addCarriageReturn(); // Quebra de linha depois
                    }
                } else {
                    novoRun.setText("O arquivo de imagem retornou vazio do servidor remetente");
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar proporção da imagem: " + e.getMessage());
                novoRun.setText("[Erro ao carregar mídia remota: " + e.getMessage() + "]");
            }
        } else {
            novoRun.setText("Nenhuma imagem anexada.");
        }
    }

    private void limparRunsDoParagrafo(XWPFParagraph paragrafo) {
        int totalRuns = paragrafo.getRuns().size();
        for (int i = totalRuns - 1; i >= 0; i--) {
            paragrafo.removeRun(i);
        }
    }
}