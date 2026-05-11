package com.pedro.ordemservico.dto.request;

import jakarta.validation.constraints.NotBlank;

public class FinalizarOrdemServicoRequest {

    @NotBlank
    private String descricaoTecnica;

    public FinalizarOrdemServicoRequest() {
    }

    public FinalizarOrdemServicoRequest(String descricaoTecnica) {
        this.descricaoTecnica = descricaoTecnica;
    }

    public String getDescricaoTecnica() {
        return descricaoTecnica;
    }

    public void setDescricaoTecnica(String descricaoTecnica) {
        this.descricaoTecnica = descricaoTecnica;
    }
}
