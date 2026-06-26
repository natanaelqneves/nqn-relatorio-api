package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.OcorrenciaViaRequestDTO;
import com.nqn.relatorios_semob.model.OcorrenciaVia;
import com.nqn.relatorios_semob.service.OcorrenciaViaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ocorrencia")
public class OcorrenciaController {

    private final OcorrenciaViaService ocorrenciaViaService;

    public OcorrenciaController(OcorrenciaViaService ocorrenciaViaService) {
        this.ocorrenciaViaService = ocorrenciaViaService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<OcorrenciaVia> adicionarOcorrencia(
            @PathVariable Long id,
            @Valid @RequestBody OcorrenciaViaRequestDTO dto
            ) {

        OcorrenciaVia ocorrencia = ocorrenciaViaService.salvarOcorrencia(id, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ocorrencia);
    }
}
