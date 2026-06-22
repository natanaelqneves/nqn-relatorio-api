package com.nqn.relatorios_semob.dto;

public record UsuarioResponseDTO(
        String nomeCompleto,
        String matricula,
        String nomeDeUsuario,
        String email,
        String assinatura
) {
}
