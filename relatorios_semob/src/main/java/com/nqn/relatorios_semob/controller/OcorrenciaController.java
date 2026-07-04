package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.OcorrenciaRequestDTO;
import com.nqn.relatorios_semob.dto.OcorrenciaResponseDTO;
import com.nqn.relatorios_semob.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ocorrencia")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<OcorrenciaResponseDTO> adicionarOcorrencia(
            @PathVariable Long relatorioId,
            @Valid @RequestBody OcorrenciaRequestDTO ocorrenticaDTO
            ) {

        OcorrenciaResponseDTO ocorrenciaResponseDTO = ocorrenciaService.salvarOcorrencia(relatorioId, ocorrenticaDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrenciaResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagarPorId(
            @PathVariable Long id){
        ocorrenciaService.apagarPorId(id);

        return ResponseEntity.noContent().build();
    }
}
