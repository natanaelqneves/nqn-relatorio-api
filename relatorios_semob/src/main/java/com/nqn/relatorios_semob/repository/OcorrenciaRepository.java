package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    void deleteByRelatorioId(Long id);
}
