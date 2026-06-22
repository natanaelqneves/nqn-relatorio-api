package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotBlank;

public record AutenticacaoDTO(
        @NotBlank(message = "O nome de login de usuário é obrigatório.")
        String nomeDeUsuario,

        @NotBlank(message = "A senha é obrigatório.")
        String senha
) {
}
