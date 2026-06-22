package com.nqn.relatorios_semob.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RelatorioRequestDTO(
        @NotBlank(message = "Data do serviço não apresentada.")
        LocalDate dataDoServico,

        @NotBlank(message = "Carga horária do plantão não declarada.")
        Integer horasPlantao,

        @NotBlank(message = "Horário de início não declarado.")
        Integer horaInicio,

        @NotBlank(message = "Viatura não declarada.")
        String placaViatura,

        @NotBlank(message = "Km Inicial não declarado.")
        Integer kmInicial,

        Integer kmFinal,

        String descricaoServico,

        String sinistro,

        String avarias
) {
}
