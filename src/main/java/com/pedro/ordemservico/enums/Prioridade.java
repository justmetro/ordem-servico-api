package com.pedro.ordemservico.enums;

public enum Prioridade {
    BAIXA(1),
    MEDIA(2),
    ALTA(3),
    CRITICA(4);

    private final int peso;

    Prioridade(int peso) {
        this.peso = peso;
    }

    public int getPeso() {
        return peso;
    }
}
