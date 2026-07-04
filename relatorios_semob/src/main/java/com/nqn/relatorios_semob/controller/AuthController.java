package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.AutenticacaoDTO;
import com.nqn.relatorios_semob.dto.TokenResponseDTO;
import com.nqn.relatorios_semob.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<TokenResponseDTO> autenticar(@RequestBody @Valid AutenticacaoDTO dto) {
        String token = authService.login(dto);

        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
