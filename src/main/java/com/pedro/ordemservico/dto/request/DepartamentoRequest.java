package com.pedro.ordemservico.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartamentoRequest {

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotBlank
    @Size(max = 20)
    private String sigla;

    public DepartamentoRequest() {
    }

    public DepartamentoRequest(String nome, String sigla) {
        this.nome = nome;
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
