package com.nqn.relatorios_semob.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RelatorioRequestDTO(
        @NotNull(message = "Data do serviço não apresentada.")
        LocalDate dataDoServico,

        @NotNull(message = "Carga horária do plantão não declarada.")
        Integer horasPlantao,

        @NotNull(message = "Horário de início não declarado.")
        Integer horaInicio,

        @NotBlank(message = "Viatura não declarada.")
        String placaViatura,

        @NotNull(message = "Km Inicial não declarado.")
        Integer kmInicial,

        Integer kmFinal,

        String descricaoServico,

        String sinistro,

        String avarias
) {
}
