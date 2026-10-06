package br.com.fiap.mercadomvc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TDS_Users_Mercado")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Column(name = "NOME_COMPLETO", nullable = false, length = 100)
    private String nomeCompleto;

    @NotBlank(message = "O nome de usuário (login) é obrigatório.")
    @Size(min = 3, max = 50, message = "O usuário deve ter entre 3 e 50 caracteres.")
    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 4, message = "A senha deve ter no mínimo 4 caracteres.")
    @Column(name = "PASSWORD", nullable = false, length = 100)
    private String password;

    @NotNull(message = "O perfil é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 30)
    private Role role;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo = true;

    public Usuario() {
        this.ativo = true;
    }

    public Usuario(Long id, String nomeCompleto, String username, String password, Role role, boolean ativo) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.username = username;
        this.password = password;
        this.role = role;
        this.ativo = ativo;
    }

    public Usuario(String nomeCompleto, String username, String password, Role role) {
        this(null, nomeCompleto, username, password, role, true);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Builder manual para garantir compilação com -proc:none
    public static UsuarioBuilder builder() {
        return new UsuarioBuilder();
    }

    public static class UsuarioBuilder {
        private Long id;
        private String nomeCompleto;
        private String username;
        private String password;
        private Role role;
        private boolean ativo = true;

        public UsuarioBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UsuarioBuilder nomeCompleto(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
            return this;
        }

        public UsuarioBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UsuarioBuilder ativo(boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Usuario build() {
            return new Usuario(id, nomeCompleto, username, password, role, ativo);
        }
    }
}
