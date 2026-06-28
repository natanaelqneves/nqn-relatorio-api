package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssinaturaDTO(
        @NotBlank(message = "A URL da assinatura não pode estar vazia")
        String assinatura) {
}
