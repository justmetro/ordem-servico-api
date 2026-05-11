package com.pedro.ordemservico.dto.response;

public class LoginResponse {

    private String token;
    private String tipo;
    private UsuarioResponse usuario;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tipo, UsuarioResponse usuario) {
        this.token = token;
        this.tipo = tipo;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UsuarioResponse getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponse usuario) {
        this.usuario = usuario;
    }
}
