package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
}
