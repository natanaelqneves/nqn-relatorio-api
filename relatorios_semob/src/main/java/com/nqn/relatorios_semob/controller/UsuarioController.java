package com.nqn.relatorios_semob.controller;


import com.nqn.relatorios_semob.dto.*;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Tag(name = "Usuário", description = "Login, cadastro, registro de assinatura para relatorio.")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastra usuário.", description = "Recebe as credenciais para acesso e serviços de e-mail")
    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @RequestBody @Valid UsuarioRequestDTO dto){

        UsuarioResponseDTO novoUsuario = usuarioService.cadastrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @Operation(summary = "Salva assinatura do usuário.", description = "Salva a assinatura que será inserida em cada relatorio doc.")
    @PutMapping("/assinatura")
    ResponseEntity<Void> salvarAssinatura(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody AssinaturaDTO dto) {

        usuarioService.salvarAssinatura(usuarioLogado, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Altera senha do usuário.", description = "Permite ao usuário logado alterar a própria senha.")
    @PutMapping("senha")
    public ResponseEntity<String> mudarSenha(
            @RequestBody MudarSenhaDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado){

        try {
            usuarioService.mudarSenha(dto, usuarioLogado);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok("Senha alterada.");
    }
}

