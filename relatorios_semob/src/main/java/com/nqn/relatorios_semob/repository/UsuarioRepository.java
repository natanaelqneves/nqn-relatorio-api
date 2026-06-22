package com.nqn.relatorios_semob.repository;

import com.nqn.relatorios_semob.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeDeUsuario(String nomeDeUsuario);

    Optional<Object> findByEmail(String email);

    Optional<Object> findByMatricula(String matricula);
}
