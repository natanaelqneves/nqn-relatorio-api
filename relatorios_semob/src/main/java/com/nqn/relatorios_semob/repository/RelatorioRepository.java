package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    Optional<Relatorio> findByIdAndUsuarioId(Long id, Long id1);

    List<Relatorio> findAllByOrderByDataDoServicoDesc();

    List<Relatorio> findByUsuarioIdOrderByDataDoServicoDesc(Long usuarioId);

    @Query("SELECT r FROM Relatorio r WHERE r.usuario.id = :usuarioId " +
            "AND r.dataDoServico BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY r.dataDoServico DESC")
    List<Relatorio> findByUsuarioIdAndPeriodo(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") java.time.LocalDate dataInicio,
            @Param("dataFim") java.time.LocalDate dataFim
    );
}
