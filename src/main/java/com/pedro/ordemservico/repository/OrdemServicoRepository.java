package com.pedro.ordemservico.repository;

import com.pedro.ordemservico.entity.OrdemServico;
import com.pedro.ordemservico.enums.Prioridade;
import com.pedro.ordemservico.enums.StatusOrdemServico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByAtivoTrue();

    Page<OrdemServico> findByAtivoTrue(Pageable pageable);

    @Query("""
            SELECT os
            FROM OrdemServico os
            WHERE os.ativo = true
              AND (:status IS NULL OR os.status = :status)
              AND (:prioridade IS NULL OR os.prioridade = :prioridade)
              AND (:tecnicoId IS NULL OR os.tecnico.id = :tecnicoId)
              AND (:departamentoId IS NULL OR os.departamento.id = :departamentoId)
              AND (:dataInicio IS NULL OR os.dataAbertura >= :dataInicio)
              AND (:dataFim IS NULL OR os.dataAbertura <= :dataFim)
            """)
    Page<OrdemServico> filtrarAtivas(
            @Param("status") StatusOrdemServico status,
            @Param("prioridade") Prioridade prioridade,
            @Param("tecnicoId") Long tecnicoId,
            @Param("departamentoId") Long departamentoId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
}
