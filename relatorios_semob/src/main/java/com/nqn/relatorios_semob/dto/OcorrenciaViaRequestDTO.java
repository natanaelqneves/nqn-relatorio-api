package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OcorrenciaViaRequestDTO(
        @NotBlank(message = "Url da imagem não apresentada.")
        String urlFoto,

        @NotBlank(message = "Descrição do fato não apresentado.")
        String descricaoDefeito,

        @NotBlank(message = "Erro ao capturar localização de latitude.")
        Double latitude,

        @NotBlank(message = "Erro ao capturar localização de longitude.")
        Double longitude
) {}