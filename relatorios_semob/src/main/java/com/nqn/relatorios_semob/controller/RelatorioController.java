package com.nqn.relatorios_semob.controller;


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

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final DocxService docxService;

    public RelatorioController(RelatorioService relatorioService, DocxService docxService) {
        this.relatorioService = relatorioService;
        this.docxService = docxService;
    }

    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> cadastrar(
            @Valid @RequestBody RelatorioRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

            RelatorioResponseDTO relatorioResponseDTO = relatorioService.cadastrar(dto,usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorioResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RelatorioRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

        RelatorioResponseDTO relatorioResponseDTO = relatorioService.atualizar(id, dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.OK).body(relatorioResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){
        Relatorio relatorio =  relatorioService.buscarPorIdEUsuario(id, usuarioLogado);
        RelatorioResponseDTO response = relatorioService.gerarRelatorioResponse(relatorio);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorDataDeServico(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(value = "dataInicio", required = false) String dataInicio,
            @RequestParam(value = "dataFim", required = false) String dataFim){
        List<RelatorioResponseDTO> relatorios = relatorioService.listarPorUsuarioEPeriodo(usuarioLogado, dataInicio, dataFim);

        return ResponseEntity.ok(relatorios);
    }

    @GetMapping("/{id}/doc")
    public ResponseEntity<byte[]> baixar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){

        Relatorio relatorio = relatorioService.buscarPorIdEUsuario(id, usuarioLogado);
        byte[] pdfBytes = docxService.gerarDocx(relatorio);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.setContentDispositionFormData("attachment", "Relatorio_Servico_" + id + ".docx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){
        Relatorio relatorio = relatorioService.buscarPorIdEUsuario(id, usuarioLogado);

        if (relatorio.getUsuario() == null || !relatorio.getUsuario().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        relatorioService.apagarRelatorio(id);
        return ResponseEntity.noContent().build();
    }
}
