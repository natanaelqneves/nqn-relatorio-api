package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.Viatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViaturaRepository extends JpaRepository<Viatura, Long> {
    Optional<Viatura> findByPlaca(String placa);
}
