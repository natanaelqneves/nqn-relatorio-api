package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OcorrenciaViaRequestDTO(
        @NotBlank
        String urlFoto,
        @NotBlank
        String descricaoDefeito,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude
) {}