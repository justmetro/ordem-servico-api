package com.pedro.ordemservico.dto.response;

import com.pedro.ordemservico.enums.StatusOrdemServico;

import java.time.LocalDateTime;

public class OrdemServicoHistoricoResponse {

    private Long id;
    private Long ordemServicoId;
    private StatusOrdemServico statusAnterior;
    private StatusOrdemServico statusNovo;
    private String alteradoPor;
    private String observacao;
    private LocalDateTime alteradoEm;

    public OrdemServicoHistoricoResponse() {
    }

    public OrdemServicoHistoricoResponse(Long id, Long ordemServicoId, StatusOrdemServico statusAnterior,
                                         StatusOrdemServico statusNovo, String alteradoPor,
                                         String observacao, LocalDateTime alteradoEm) {
        this.id = id;
        this.ordemServicoId = ordemServicoId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.alteradoPor = alteradoPor;
        this.observacao = observacao;
        this.alteradoEm = alteradoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrdemServicoId() {
        return ordemServicoId;
    }

    public void setOrdemServicoId(Long ordemServicoId) {
        this.ordemServicoId = ordemServicoId;
    }

    public StatusOrdemServico getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusOrdemServico statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusOrdemServico getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusOrdemServico statusNovo) {
        this.statusNovo = statusNovo;
    }

    public String getAlteradoPor() {
        return alteradoPor;
    }

    public void setAlteradoPor(String alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }
}
