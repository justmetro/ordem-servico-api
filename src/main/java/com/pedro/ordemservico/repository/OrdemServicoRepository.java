package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByAtivoTrue();
}
