package br.com.fiap.mercadomvc;

import br.com.fiap.mercadomvc.model.Role;
import br.com.fiap.mercadomvc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void rotaLoginDeveRetornarSucesso() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void rotaRegistroDeveRetornarFormularioSignUp() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().attributeExists("usuarioDto", "roles"));
    }

    @Test
    void cadastrarNovoUsuarioComSucessoDevePersistirERedirecionar() throws Exception {
        mockMvc.perform(post("/registro")
                        .with(csrf())
                        .param("nomeCompleto", "Carlos Eduardo")
                        .param("username", "carlos.eduardo")
                        .param("password", "senha123")
                        .param("confirmarPassword", "senha123")
                        .param("role", "ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("sucessoMensagem"));

        assertThat(usuarioRepository.findByUsername("carlos.eduardo")).isPresent();
        assertThat(usuarioRepository.findByUsername("carlos.eduardo").get().getRole()).isEqualTo(Role.ROLE_USER);
    }

    @Test
    void cadastrarUsuarioComSenhasDivergentesDeveRetornarErros() throws Exception {
        mockMvc.perform(post("/registro")
                        .with(csrf())
                        .param("nomeCompleto", "Marina Lima")
                        .param("username", "marina.lima")
                        .param("password", "senha123")
                        .param("confirmarPassword", "senhaDiferente")
                        .param("role", "ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(view().name("registro"))
                .andExpect(model().hasErrors());
    }

    @Test
    void rotaAreaRestritaSemAutenticacaoDeveRedirecionarParaLogin() throws Exception {
        mockMvc.perform(get("/area-restrita"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void rotaAreaRestritaAutenticadoComoAdminDeveRetornarSucesso() throws Exception {
        mockMvc.perform(get("/area-restrita"))
                .andExpect(status().isOk())
                .andExpect(view().name("area-restrita"))
                .andExpect(model().attributeExists("totalProdutos", "precoMedio", "totalUsuarios"));
    }

    @Test
    @WithMockUser(username = "usuario", roles = {"USER"})
    void rotaAreaRestritaAutenticadoComoUserDeveRetornarSucesso() throws Exception {
        mockMvc.perform(get("/area-restrita"))
                .andExpect(status().isOk())
                .andExpect(view().name("area-restrita"));
    }

    @Test
    void autenticacaoComUsuarioDoBancoDeveTerSucessoERedirecionarParaAreaRestrita() throws Exception {
        // O usuario 'admin' foi semeado pelo DatabaseSeeder
        mockMvc.perform(formLogin("/login").user("admin").password("admin123"))
                .andExpect(authenticated().withUsername("admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/area-restrita"));
    }

    @Test
    void autenticacaoComSenhaInvalidaDeveFalhar() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("senhaErrada"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }
}
