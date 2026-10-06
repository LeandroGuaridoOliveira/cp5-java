package br.com.fiap.mercadomvc.dto;

import br.com.fiap.mercadomvc.model.Role;
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
public class UsuarioRegistroDto {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 4, message = "A senha deve ter no mínimo 4 caracteres.")
    private String password;

    @NotBlank(message = "A confirmação de senha é obrigatória.")
    private String confirmarPassword;

    @NotNull(message = "Selecione o perfil de usuário.")
    private Role role;

    public UsuarioRegistroDto() {
    }

    public UsuarioRegistroDto(String nomeCompleto, String username, String password, String confirmarPassword, Role role) {
        this.nomeCompleto = nomeCompleto;
        this.username = username;
        this.password = password;
        this.confirmarPassword = confirmarPassword;
        this.role = role;
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

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static UsuarioRegistroDtoBuilder builder() {
        return new UsuarioRegistroDtoBuilder();
    }

    public static class UsuarioRegistroDtoBuilder {
        private String nomeCompleto;
        private String username;
        private String password;
        private String confirmarPassword;
        private Role role;

        public UsuarioRegistroDtoBuilder nomeCompleto(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
            return this;
        }

        public UsuarioRegistroDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UsuarioRegistroDtoBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UsuarioRegistroDtoBuilder confirmarPassword(String confirmarPassword) {
            this.confirmarPassword = confirmarPassword;
            return this;
        }

        public UsuarioRegistroDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UsuarioRegistroDto build() {
            return new UsuarioRegistroDto(nomeCompleto, username, password, confirmarPassword, role);
        }
    }
}
