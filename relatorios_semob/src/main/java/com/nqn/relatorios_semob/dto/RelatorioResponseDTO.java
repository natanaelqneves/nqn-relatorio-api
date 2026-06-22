package com.nqn.relatorios_semob.dto;

import com.nqn.relatorios_semob.model.OcorrenciaVia;

import java.time.LocalDate;
import java.util.List;

public record RelatorioResponseDTO(
        Long id,
        String nomeCompleto,
        String matricula,
        String email,
        LocalDate dataDoServico,
        Integer horasPlantao,
        Integer horaInicio,
        Integer horaFinal,
        String placaViatura,
        Integer kmInicial,
        Integer kmFinal,
        String descricaoServico,
        String sinistro,
        String avarias,
        List<OcorrenciaVia> ocorrencias
) {}