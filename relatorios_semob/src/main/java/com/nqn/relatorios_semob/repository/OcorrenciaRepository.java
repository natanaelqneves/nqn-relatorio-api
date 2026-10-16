package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    void deleteByRelatorioId(Long id);
}
