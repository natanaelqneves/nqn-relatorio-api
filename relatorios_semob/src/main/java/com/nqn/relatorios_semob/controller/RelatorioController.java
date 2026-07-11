package com.nqn.relatorios_semob.controller;


import com.nqn.relatorios_semob.dto.RelatorioRequestDTO;
import com.nqn.relatorios_semob.dto.RelatorioResponseDTO;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.DocxService;
import com.nqn.relatorios_semob.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Relatórios", description = "Criação, e manipulação de relatorios e emissão de documentos.")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final DocxService docxService;

    public RelatorioController(RelatorioService relatorioService, DocxService docxService) {
        this.relatorioService = relatorioService;
        this.docxService = docxService;
    }

    @Operation(summary = "Cadastra novo relatório.", description = "Recebe os dados inseridos pelo agente de serviço.")
    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> cadastrar(
            @Valid @RequestBody RelatorioRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

            RelatorioResponseDTO relatorioResponseDTO = relatorioService.cadastrar(dto,usuarioLogado);
            return ResponseEntity.status(HttpStatus.CREATED).body(relatorioResponseDTO);
    }

    @Operation(summary = "Atualiza relatório existente.", description = "Recebe os dados alterados pelo agente de serviço.")
    @PutMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RelatorioRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

        RelatorioResponseDTO relatorioResponseDTO = relatorioService.atualizar(id, dto, usuarioLogado);
        return ResponseEntity.status(HttpStatus.OK).body(relatorioResponseDTO);
    }

    @Operation(summary = "Busca relatório.", description = "Busca relatório específico por ID.")
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){

        Relatorio relatorio =  relatorioService.buscarPorIdEUsuario(id, usuarioLogado);
        RelatorioResponseDTO response = relatorioService.gerarRelatorioResponse(relatorio);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Busca relatórios.", description = "Busca relatórios por interfalo de datas específicas de lançamento.")
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarPorDataDeServico(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestParam(value = "dataInicio", required = false) String dataInicio,
            @RequestParam(value = "dataFim", required = false) String dataFim){

        List<RelatorioResponseDTO> relatorios = relatorioService.listarPorUsuarioEPeriodo(usuarioLogado, dataInicio, dataFim);

        return ResponseEntity.ok(relatorios);
    }

    @Operation(summary = "Baixar relatório.", description = "Baixa relatório específico por ID.")
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

    @Operation(summary = "Apaga relatório.", description = "Apaga relatório específico por ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado){

        Relatorio relatorio = relatorioService.buscarPorIdEUsuario(id, usuarioLogado);

        if (relatorio.getUsuario() == null || !relatorio.getUsuario().getId().equals(usuarioLogado.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        relatorioService.apagarRelatorio(id, usuarioLogado);

        return ResponseEntity.noContent().build();
    }
}
