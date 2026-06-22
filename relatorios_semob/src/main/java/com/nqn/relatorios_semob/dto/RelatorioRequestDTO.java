package com.nqn.relatorios_semob.dto;

import jakarta.persistence.Column;

import java.time.LocalDate;

public record RelatorioRequestDTO(
        LocalDate dataDoServico,
        Integer horasPlantao,
        Integer horaInicio,
        String placaViatura,
        Integer kmInicial,
        Integer kmFinal,
        String descricaoServico,
        String sinistro,
        String avarias
) {
}
