package com.nqn.relatorios_semob.controller;


import com.nqn.relatorios_semob.dto.OcorrenciaViaRequestDTO;
import com.nqn.relatorios_semob.dto.RelatorioRequestDTO;
import com.nqn.relatorios_semob.dto.RelatorioResponseDTO;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.DocxService;
import com.nqn.relatorios_semob.service.OcorrenciaViaService;
import com.nqn.relatorios_semob.service.RelatorioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final DocxService docxService;
    private final OcorrenciaViaService ocorrenciaViaService;

    public RelatorioController(RelatorioService relatorioService, DocxService docxService, OcorrenciaViaService ocorrenciaViaService) {
        this.relatorioService = relatorioService;
        this.docxService = docxService;
        this.ocorrenciaViaService = ocorrenciaViaService;
    }

    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> cadastrarRelatorio(
            @RequestBody RelatorioRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

            RelatorioResponseDTO relatorioResponseDTO = relatorioService.cadastrar(dto,usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorioResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> atualizarRelatorio(
            @PathVariable Long id,
            @RequestBody RelatorioRequestDTO dto){

        RelatorioResponseDTO relatorioResponseDTO = relatorioService.atualizar(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(relatorioResponseDTO);
    }


    @PostMapping("/{id}/ocorrencias-via")
    public ResponseEntity<Void> adicionarOcorrencia(
            @PathVariable Long id,
            @Valid @RequestBody OcorrenciaViaRequestDTO dto) {

        // Agora delega a responsabilidade para o service correto!
        ocorrenciaViaService.salvarOcorrencia(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){
        Relatorio relatorio =  relatorioService.buscarPorIdEUsuario(id, usuarioLogado);
        RelatorioResponseDTO response = relatorioService.converteRelatorioResponse(relatorio);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarRelatoriosPorDataDeServico(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(value = "dataInicio", required = false) String dataInicio,
            @RequestParam(value = "dataFim", required = false) String dataFim){
        List<RelatorioResponseDTO> relatorios = relatorioService.listarRelatoriosPorUsuarioEPeriodo(usuarioLogado, dataInicio, dataFim);
        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/{id}/doc")
    public ResponseEntity<byte[]> baixarPdfRelatorio(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){

        Relatorio relatorio = relatorioService.buscarPorIdEUsuario(id, usuarioLogado);
        byte[] pdfBytes = docxService.gerarDocx(relatorio);

        // Configura os cabeçalhos HTTP para o navegador entender que é um arquivo PDF para download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        // 2. Altera a extensão do anexo para .docx
        headers.setContentDispositionFormData("attachment", "Relatorio_Servico_" + id + ".docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){
        Relatorio relatorio = relatorioService.buscarPorIdEUsuario(id, usuarioLogado);

        // Validação de segurança que garante que o relatório é do próprio usuário logado
        if (relatorio.getUsuario() == null || !relatorio.getUsuario().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        relatorioService.apagarRelatorio(id);
        return ResponseEntity.noContent().build();
    }
}
