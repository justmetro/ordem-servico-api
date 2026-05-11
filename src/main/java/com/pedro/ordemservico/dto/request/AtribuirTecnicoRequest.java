package com.pedro.ordemservico.dto.request;

import jakarta.validation.constraints.NotNull;

public class AtribuirTecnicoRequest {

    @NotNull
    private Long tecnicoId;

    public AtribuirTecnicoRequest() {
    }

    public AtribuirTecnicoRequest(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }
}
