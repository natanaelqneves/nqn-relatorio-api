package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class EnvioLoteEmailDTO {

    @NotEmpty(message = "É necessário selecionar pelo menos um relatório para envio.")
    private List<Long> idsRelatorios;

    // Construtores
    public EnvioLoteEmailDTO() {}

    public EnvioLoteEmailDTO(List<Long> idsRelatorios) {
        this.idsRelatorios = idsRelatorios;
    }

    public List<Long> getIdsRelatorios() {
        return idsRelatorios;
    }

    public void setIdsRelatorios(List<Long> idsRelatorios) {
        this.idsRelatorios = idsRelatorios;
    }
}