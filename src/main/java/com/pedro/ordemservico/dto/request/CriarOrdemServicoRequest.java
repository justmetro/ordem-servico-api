package com.pedro.ordemservico.dto.request;

import com.pedro.ordemservico.enums.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CriarOrdemServicoRequest {

    @NotBlank
    @Size(max = 160)
    private String titulo;

    @NotBlank
    private String descricao;

    @NotBlank
    @Size(max = 120)
    private String solicitante;

    @NotNull
    private Prioridade prioridade;

    @NotNull
    private Long departamentoId;

    private Long tecnicoId;

    public CriarOrdemServicoRequest() {
    }

    public CriarOrdemServicoRequest(String titulo, String descricao, String solicitante,
                                    Prioridade prioridade, Long departamentoId, Long tecnicoId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.prioridade = prioridade;
        this.departamentoId = departamentoId;
        this.tecnicoId = tecnicoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
}
