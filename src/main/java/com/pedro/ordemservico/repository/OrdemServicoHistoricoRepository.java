package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.OrdemServicoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoHistoricoRepository extends JpaRepository<OrdemServicoHistorico, Long> {

    List<OrdemServicoHistorico> findByOrdemServicoIdOrderByAlteradoEmAsc(Long ordemServicoId);
}
