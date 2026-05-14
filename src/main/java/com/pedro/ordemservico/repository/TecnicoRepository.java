package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    boolean existsByEmail(String email);

    List<Tecnico> findByAtivoTrue();

    Optional<Tecnico> findByUsuarioEmail(String email);
}
