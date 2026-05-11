package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    boolean existsBySigla(String sigla);

    List<Departamento> findByAtivoTrue();
}
