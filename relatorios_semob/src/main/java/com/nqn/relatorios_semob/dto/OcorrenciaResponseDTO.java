package com.nqn.relatorios_semob.dto;

public record OcorrenciaResponseDTO(
        Long id,
        String descricaoDefeito,
        String urlFoto,
        Double latitude,
        Double longitude
) {}
