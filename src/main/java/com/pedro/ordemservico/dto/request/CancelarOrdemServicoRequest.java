package com.pedro.ordemservico.dto.request;

import jakarta.validation.constraints.Size;

public class CancelarOrdemServicoRequest {

    @Size(max = 500)
    private String motivo;

    public CancelarOrdemServicoRequest() {
    }

    public CancelarOrdemServicoRequest(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
