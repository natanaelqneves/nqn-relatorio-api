package com.nqn.relatorios_semob.dto;

import jakarta.persistence.Column;

public record ViaturaResponseDTO(
        Long id,
        String placa,
        String modelo,
        String ano,
        Integer kmAtual
) {
}
