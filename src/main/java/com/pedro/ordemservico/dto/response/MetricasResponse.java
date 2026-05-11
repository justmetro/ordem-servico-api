package com.pedro.ordemservico.dto.response;

import java.util.Map;

public class MetricasResponse {

    private Map<String, Long> totalPorStatus;
    private Map<String, Long> totalPorPrioridade;
    private Map<String, Long> totalPorTecnico;
    private Double tempoMedioResolucaoHoras;
    private Long ordensAbertasUltimos30Dias;
    private Double tempoMedioFilaHoras;
    private Double percentualSlaCumprido;
    private Map<String, Long> cargaPorTecnico;

    public MetricasResponse() {
    }

    public MetricasResponse(Map<String, Long> totalPorStatus, Map<String, Long> totalPorPrioridade,
                            Map<String, Long> totalPorTecnico, Double tempoMedioResolucaoHoras,
                            Long ordensAbertasUltimos30Dias, Double tempoMedioFilaHoras,
                            Double percentualSlaCumprido, Map<String, Long> cargaPorTecnico) {
        this.totalPorStatus = totalPorStatus;
        this.totalPorPrioridade = totalPorPrioridade;
        this.totalPorTecnico = totalPorTecnico;
        this.tempoMedioResolucaoHoras = tempoMedioResolucaoHoras;
        this.ordensAbertasUltimos30Dias = ordensAbertasUltimos30Dias;
        this.tempoMedioFilaHoras = tempoMedioFilaHoras;
        this.percentualSlaCumprido = percentualSlaCumprido;
        this.cargaPorTecnico = cargaPorTecnico;
    }

    public Map<String, Long> getTotalPorStatus() {
        return totalPorStatus;
    }

    public void setTotalPorStatus(Map<String, Long> totalPorStatus) {
        this.totalPorStatus = totalPorStatus;
    }

    public Map<String, Long> getTotalPorPrioridade() {
        return totalPorPrioridade;
    }

    public void setTotalPorPrioridade(Map<String, Long> totalPorPrioridade) {
        this.totalPorPrioridade = totalPorPrioridade;
    }

    public Map<String, Long> getTotalPorTecnico() {
        return totalPorTecnico;
    }

    public void setTotalPorTecnico(Map<String, Long> totalPorTecnico) {
        this.totalPorTecnico = totalPorTecnico;
    }

    public Double getTempoMedioResolucaoHoras() {
        return tempoMedioResolucaoHoras;
    }

    public void setTempoMedioResolucaoHoras(Double tempoMedioResolucaoHoras) {
        this.tempoMedioResolucaoHoras = tempoMedioResolucaoHoras;
    }

    public Long getOrdensAbertasUltimos30Dias() {
        return ordensAbertasUltimos30Dias;
    }

    public void setOrdensAbertasUltimos30Dias(Long ordensAbertasUltimos30Dias) {
        this.ordensAbertasUltimos30Dias = ordensAbertasUltimos30Dias;
    }

    public Double getTempoMedioFilaHoras() {
        return tempoMedioFilaHoras;
    }

    public void setTempoMedioFilaHoras(Double tempoMedioFilaHoras) {
        this.tempoMedioFilaHoras = tempoMedioFilaHoras;
    }

    public Double getPercentualSlaCumprido() {
        return percentualSlaCumprido;
    }

    public void setPercentualSlaCumprido(Double percentualSlaCumprido) {
        this.percentualSlaCumprido = percentualSlaCumprido;
    }

    public Map<String, Long> getCargaPorTecnico() {
        return cargaPorTecnico;
    }

    public void setCargaPorTecnico(Map<String, Long> cargaPorTecnico) {
        this.cargaPorTecnico = cargaPorTecnico;
    }
}
