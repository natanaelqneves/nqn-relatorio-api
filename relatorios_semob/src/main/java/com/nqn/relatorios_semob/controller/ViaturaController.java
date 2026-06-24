package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.RelatorioResponseDTO;
import com.nqn.relatorios_semob.dto.ViaturaResponseDTO;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.model.Viatura;
import com.nqn.relatorios_semob.service.ViaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viatura")
public class ViaturaController {

    private ViaturaService viaturaService;

    public ViaturaController(ViaturaService viaturaService) {
        this.viaturaService = viaturaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Viatura> buscarViaturaPorId(@PathVariable Long id){
        Viatura response = viaturaService.buscarViaturaPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ViaturaResponseDTO>> listarViaturasDisponíveis(){
        List<ViaturaResponseDTO> viaturas = viaturaService.buscarViaturas();

        return ResponseEntity.ok(viaturas);
    }
}
