package com.nqn.relatorios_semob.controller;


import com.nqn.relatorios_semob.dto.*;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @RequestBody @Valid UsuarioRequestDTO dto){

        UsuarioResponseDTO novoUsuario = usuarioService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PutMapping("/assinatura")
    ResponseEntity<Void> salvarAssinatura(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody AssinaturaDTO dto) {

        usuarioService.salvarAssinatura(usuarioLogado, dto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("senha/{id}")
    public ResponseEntity<String> mudarSenha(
            @PathVariable Long id,
            @RequestBody MudarSenhaDTO dto){
        try{
            usuarioService.mudarSenha(id, dto);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Senha alterada.");
    }
}

