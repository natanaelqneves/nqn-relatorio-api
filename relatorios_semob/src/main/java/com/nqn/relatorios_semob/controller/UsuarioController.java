package com.nqn.relatorios_semob.controller;


import com.nqn.relatorios_semob.dto.AssinaturaDTO;
import com.nqn.relatorios_semob.dto.UsuarioRequestDTO;
import com.nqn.relatorios_semob.dto.UsuarioResponseDTO;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

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

    @PostMapping("/assinatura")
    ResponseEntity<Void> salvarAssinatura(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody AssinaturaDTO dto) {

        usuarioService.salvarAssinatura(usuarioLogado, dto);

        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    public ResponseEntity<List<Usuario>> listarUsuarios(){
//        List<Usuario> usuarios = usuarioService.listarUsuarios();
//        return ResponseEntity.ok(usuarios);
//    }
}

