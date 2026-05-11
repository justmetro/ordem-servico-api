package com.pedro.ordemservico.dto.response;

import java.time.LocalDateTime;

public class TecnicoResponse {

    private Long id;
    private String nome;
    private String email;
    private String especialidade;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private Long usuarioId;

    public TecnicoResponse() {
    }

    public TecnicoResponse(Long id, String nome, String email, String especialidade,
                           Boolean ativo, LocalDateTime criadoEm, Long usuarioId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.especialidade = especialidade;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.usuarioId = usuarioId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
