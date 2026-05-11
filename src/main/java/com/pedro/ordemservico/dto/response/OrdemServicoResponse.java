package com.pedro.ordemservico.dto.response;

import com.pedro.ordemservico.enums.Prioridade;
import com.pedro.ordemservico.enums.StatusOrdemServico;

import java.time.LocalDateTime;

public class OrdemServicoResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private String solicitante;
    private Prioridade prioridade;
    private StatusOrdemServico status;
    private Long departamentoId;
    private String departamentoNome;
    private Long tecnicoId;
    private String tecnicoNome;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinalizacao;
    private String descricaoTecnica;
    private LocalDateTime atualizadoEm;
    private String atualizadoPor;
    private Boolean ativo;
    private Long version;
    private Double tempoResolucaoHoras;

    public OrdemServicoResponse() {
    }

    public OrdemServicoResponse(Long id, String titulo, String descricao, String solicitante,
                                Prioridade prioridade, StatusOrdemServico status, Long departamentoId,
                                String departamentoNome, Long tecnicoId, String tecnicoNome,
                                LocalDateTime dataAbertura, LocalDateTime dataInicio,
                                LocalDateTime dataFinalizacao, String descricaoTecnica,
                                LocalDateTime atualizadoEm, String atualizadoPor, Boolean ativo,
                                Long version, Double tempoResolucaoHoras) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.prioridade = prioridade;
        this.status = status;
        this.departamentoId = departamentoId;
        this.departamentoNome = departamentoNome;
        this.tecnicoId = tecnicoId;
        this.tecnicoNome = tecnicoNome;
        this.dataAbertura = dataAbertura;
        this.dataInicio = dataInicio;
        this.dataFinalizacao = dataFinalizacao;
        this.descricaoTecnica = descricaoTecnica;
        this.atualizadoEm = atualizadoEm;
        this.atualizadoPor = atualizadoPor;
        this.ativo = ativo;
        this.version = version;
        this.tempoResolucaoHoras = tempoResolucaoHoras;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNome() {
        return departamentoNome;
    }

    public void setDepartamentoNome(String departamentoNome) {
        this.departamentoNome = departamentoNome;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getTecnicoNome() {
        return tecnicoNome;
    }

    public void setTecnicoNome(String tecnicoNome) {
        this.tecnicoNome = tecnicoNome;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public String getDescricaoTecnica() {
        return descricaoTecnica;
    }

    public void setDescricaoTecnica(String descricaoTecnica) {
        this.descricaoTecnica = descricaoTecnica;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Double getTempoResolucaoHoras() {
        return tempoResolucaoHoras;
    }

    public void setTempoResolucaoHoras(Double tempoResolucaoHoras) {
        this.tempoResolucaoHoras = tempoResolucaoHoras;
    }
}
