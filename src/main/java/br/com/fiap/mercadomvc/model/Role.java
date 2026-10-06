package br.com.fiap.mercadomvc.model;

public enum Role {
    ROLE_ADMIN("Administrador"),
    ROLE_USER("Usuário Padrão");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
