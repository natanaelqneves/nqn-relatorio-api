package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OcorrenciaViaRequestDTO(
        @NotBlank(message = "A URL da imagem é obrigatória.")
        String urlFoto,
        @NotBlank(message = "A descrição da ocorrência é obrigatória.")
        String descricaoDefeito,
        @NotNull(message = "Erro ao enviar dados de latitude.")
        Double latitude,
        @NotNull(message = "Erro ao enviar dados de longitude.")
        Double longitude
) {}