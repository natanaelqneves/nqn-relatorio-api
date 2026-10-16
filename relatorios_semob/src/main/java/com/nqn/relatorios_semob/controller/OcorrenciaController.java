package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.OcorrenciaRequestDTO;
import com.nqn.relatorios_semob.dto.OcorrenciaResponseDTO;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ocorrencia")
@Tag(name = "Ocorrências", description = "Criação, e manipulação de ocorrências registradas no sistema.")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @Operation(summary = "Adiciona nova ocorrência.", description = "Registra os dados de novas ocorrencias.")
    @PostMapping("/{relatorioId}")
    public ResponseEntity<OcorrenciaResponseDTO> cadastrar(
            @PathVariable Long relatorioId,
            @Valid @RequestBody OcorrenciaRequestDTO ocorrenticaDTO
            ) {

        OcorrenciaResponseDTO ocorrenciaResponseDTO = ocorrenciaService.cadastrar(relatorioId, ocorrenticaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrenciaResponseDTO);
    }

    @Operation(summary = "Busca ocorrências.", description = "Busca ocorrências por relatório de usuário.")
    @GetMapping
    public ResponseEntity<List<OcorrenciaResponseDTO>> listarPorRelatorioDeUsuario(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable Long relatorioId){

        List<OcorrenciaResponseDTO> ocorrencias = ocorrenciaService.listarOcorrenciasPorRelatorioDeUsuario(usuarioLogado, relatorioId);

        return ResponseEntity.ok(ocorrencias);
    }

    @Operation(summary = "Apaga ocorrencias.", description = "Opção para apagar dados equivocados ou desnecessários.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPorId(
            @PathVariable Long id){
        ocorrenciaService.apagarPorId(id);

        return ResponseEntity.noContent().build();
    }
}
