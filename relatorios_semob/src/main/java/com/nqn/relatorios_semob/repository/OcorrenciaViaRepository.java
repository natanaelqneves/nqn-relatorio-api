package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.OcorrenciaVia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaViaRepository extends JpaRepository<OcorrenciaVia, Long> {
    void deleteByRelatorioId(Long id);
}
