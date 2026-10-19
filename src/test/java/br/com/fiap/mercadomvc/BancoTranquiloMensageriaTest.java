package br.com.fiap.mercadomvc;

import br.com.fiap.mercadomvc.dto.CompraRequestDto;
import br.com.fiap.mercadomvc.dto.ItemCompraDto;
import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.StatusPagamento;
import br.com.fiap.mercadomvc.repository.CompraRepository;
import br.com.fiap.mercadomvc.repository.EmailLogRepository;
import br.com.fiap.mercadomvc.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BancoTranquiloMensageriaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Test
    void endpointStatusBancoTranquiloDeveRetornarOnlineEConfiguracoesRabbitMQ() throws Exception {
        mockMvc.perform(get("/api/banco-tranquilo/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"))
                .andExpect(jsonPath("$.broker").value("RabbitMQ"))
                .andExpect(jsonPath("$.exchange").value("mercado.banco.exchange"))
                .andExpect(jsonPath("$.queueRequisicao").value("banco.tranquilo.pagamento.requisicao.queue"))
                .andExpect(jsonPath("$.queueResposta").value("banco.tranquilo.pagamento.resposta.queue"));
    }

    @Test
    void realizarCompraViaApiJsonDeveAprovarNoBancoTranquiloEArmazenarEmailNoBanco() throws Exception {
        Produto prod = produtoRepository.findAll().get(0);
        long emailsAntes = emailLogRepository.count();

        CompraRequestDto request = new CompraRequestDto(
                "Cliente Teste Postman",
                "cliente.postman@fiap.com.br",
                "111.222.333-44",
                "PIX",
                null,
                List.of(new ItemCompraDto(prod.getId(), 2))
        );

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.compra.statusPagamento").value("APROVADO"))
                .andExpect(jsonPath("$.compra.emailEnviado").value(true))
                .andExpect(jsonPath("$.compra.protocoloBanco").isNotEmpty());

        assertThat(emailLogRepository.count()).isGreaterThan(emailsAntes);
        assertThat(emailLogRepository.findByDestinatarioContainingIgnoreCaseOrderByDataEnvioDesc("cliente.postman@fiap.com.br"))
                .isNotEmpty();
    }

    @Test
    void realizarCompraComCartaoFinal0000DeveSerRecusadaPeloBancoTranquiloENaoEnviarEmail() throws Exception {
        Produto prod = produtoRepository.findAll().get(0);
        long emailsAntes = emailLogRepository.count();

        CompraRequestDto request = new CompraRequestDto(
                "Cliente Cartao Bloqueado",
                "recusado@fiap.com.br",
                "999.888.777-66",
                "CARTAO_CREDITO",
                "5432 1098 7654 0000",
                List.of(new ItemCompraDto(prod.getId(), 1))
        );

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.compra.statusPagamento").value(StatusPagamento.RECUSADO.name()))
                .andExpect(jsonPath("$.compra.emailEnviado").value(false));

        assertThat(emailLogRepository.count()).isEqualTo(emailsAntes);
    }

    @Test
    void telaCarrinhoCheckoutDeveCarregarComSucesso() throws Exception {
        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isOk())
                .andExpect(view().name("carrinho/checkout"))
                .andExpect(model().attributeExists("itensCarrinho", "valorTotal", "produtosDisponiveis"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void dashboardGerenciaDeveExibirGraficosDeVendasLucrosEstoqueEEmails() throws Exception {
        mockMvc.perform(get("/dashboard-gerencia"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/gerencia"))
                .andExpect(model().attributeExists(
                        "compras",
                        "emails",
                        "receitaTotal",
                        "lucroTotal",
                        "estoqueTotalItens",
                        "metodosLabels",
                        "nomesProdutos"
                ));
    }
}
