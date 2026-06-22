package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.UsuarioRequestDTO;
import com.nqn.relatorios_semob.dto.UsuarioResponseDTO;
import com.nqn.relatorios_semob.service.CriptografiaUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gerarsenha")
public class GerarSenhaLocalController {

    private CriptografiaUtil criptografiaUtil;

    public GerarSenhaLocalController(CriptografiaUtil criptografiaUtil) {
        this.criptografiaUtil = criptografiaUtil;
    }

    @PostMapping
    public ResponseEntity<String> gerarSenha(@RequestParam String senha){
        String senhaGerada =  criptografiaUtil.criptografar(senha);

        return ResponseEntity.status(HttpStatus.CREATED).body(senhaGerada);
    }
}
