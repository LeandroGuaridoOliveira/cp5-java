package br.com.fiap.mercadomvc.config;

import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.Role;
import br.com.fiap.mercadomvc.model.StatusPagamento;
import br.com.fiap.mercadomvc.repository.CompraRepository;
import br.com.fiap.mercadomvc.repository.ProdutoRepository;
import br.com.fiap.mercadomvc.service.EmailNotificationService;
import br.com.fiap.mercadomvc.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;
    private final CompraRepository compraRepository;
    private final EmailNotificationService emailNotificationService;

    public DatabaseSeeder(ProdutoRepository produtoRepository,
                          UsuarioService usuarioService,
                          CompraRepository compraRepository,
                          EmailNotificationService emailNotificationService) {
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
        this.compraRepository = compraRepository;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public void run(String... args) {
        // Carga inicial de usuários na tabela TDS_Users_Mercado
        if (usuarioService.totalUsuarios() == 0) {
            usuarioService.salvarUsuarioSeeder(
                "Administrador Geral",
                "admin",
                "admin123",
                Role.ROLE_ADMIN
            );

            usuarioService.salvarUsuarioSeeder(
                "Usuário Operador",
                "usuario",
                "user123",
                Role.ROLE_USER
            );
        }

        // Carga inicial de produtos na tabela TDS_Sec_MVC_TB_Mercado
        if (produtoRepository.count() == 0) {
            produtoRepository.saveAll(List.of(
                new Produto("Sabão em Pó Omo Lavagem Perfeita", "Produto de Limpeza", "Limpeza", "1.6kg", 24.90, 45),
                new Produto("Detergente Líquido Neutro Ypê", "Produto de Limpeza", "Limpeza", "500ml", 2.89, 120),
                new Produto("Kit Meia Cano Médio Lupo (3 pares)", "Vestuário", "Meias e Acessórios", "39-43", 29.90, 60),
                new Produto("Maçã Fuji Selecionada", "Hortifruti", "Frutas e Verduras", "1kg", 9.80, 85),
                new Produto("Banana Prata Premium", "Hortifruti", "Frutas e Verduras", "1kg", 6.50, 90),
                new Produto("Carrinho de Controle Remoto Speed", "Brinquedo", "Brinquedos", "Único", 89.90, 25),
                new Produto("Jogo de Tabuleiro Clássico", "Brinquedo", "Brinquedos", "Médio", 49.99, 30),
                new Produto("Desinfetante Pinho Sol Original", "Produto de Limpeza", "Limpeza", "1L", 11.40, 75)
            ));
        }

        // Carga inicial de compras e e-mails confirmados para demonstração imediata do Dashboard Gerencial (Parte 2)
        if (compraRepository.count() == 0) {
            Compra c1 = new Compra();
            c1.setCodigoTransacao("TRX-CP5001A");
            c1.setNomeCliente("Leandro Guarido");
            c1.setEmailCliente("leandro.guarido@fiap.com.br");
            c1.setCpfCliente("123.456.789-00");
            c1.setMetodoPagamento("PIX");
            c1.setItensResumo("2x Sabão em Pó Omo Lavagem Perfeita (R$ 24,90), 1x Kit Meia Cano Médio Lupo (R$ 29,90)");
            c1.setQuantidadeTotal(3);
            c1.setValorTotal(79.70);
            c1.setLucroEstimado(27.90);
            c1.setStatusPagamento(StatusPagamento.APROVADO);
            c1.setProtocoloBanco("BT-98A4F210");
            c1.setMensagemBanco("Pagamento confirmado com sucesso via PIX pelo Banco Tranquilo.");
            c1.setDataCompra(LocalDateTime.now().minusHours(3));
            c1.setDataConfirmacao(LocalDateTime.now().minusHours(3).plusSeconds(2));
            c1.setEmailEnviado(true);
            c1 = compraRepository.save(c1);
            emailNotificationService.enviarEmailConfirmacaoPagamento(c1);

            Compra c2 = new Compra();
            c2.setCodigoTransacao("TRX-CP5002B");
            c2.setNomeCliente("Kaiky Pereira Rodrigues Da Silva");
            c2.setEmailCliente("kaiky.rodrigues@fiap.com.br");
            c2.setCpfCliente("234.567.890-11");
            c2.setMetodoPagamento("CARTAO_CREDITO");
            c2.setNumeroCartaoFinal("4589");
            c2.setItensResumo("1x Carrinho de Controle Remoto Speed (R$ 89,90), 2x Maçã Fuji Selecionada (R$ 9,80)");
            c2.setQuantidadeTotal(3);
            c2.setValorTotal(109.50);
            c2.setLucroEstimado(38.33);
            c2.setStatusPagamento(StatusPagamento.APROVADO);
            c2.setProtocoloBanco("BT-77C1D904");
            c2.setMensagemBanco("Pagamento confirmado com sucesso via CARTAO_CREDITO pelo Banco Tranquilo.");
            c2.setDataCompra(LocalDateTime.now().minusHours(2));
            c2.setDataConfirmacao(LocalDateTime.now().minusHours(2).plusSeconds(3));
            c2.setEmailEnviado(true);
            c2 = compraRepository.save(c2);
            emailNotificationService.enviarEmailConfirmacaoPagamento(c2);

            Compra c3 = new Compra();
            c3.setCodigoTransacao("TRX-CP5003C");
            c3.setNomeCliente("Gabriel Costa Solano");
            c3.setEmailCliente("gabriel.solano@fiap.com.br");
            c3.setCpfCliente("345.678.901-22");
            c3.setMetodoPagamento("CARTAO_DEBITO");
            c3.setNumeroCartaoFinal("8821");
            c3.setItensResumo("1x Jogo de Tabuleiro Clássico (R$ 49,99), 3x Desinfetante Pinho Sol Original (R$ 11,40)");
            c3.setQuantidadeTotal(4);
            c3.setValorTotal(84.19);
            c3.setLucroEstimado(29.47);
            c3.setStatusPagamento(StatusPagamento.APROVADO);
            c3.setProtocoloBanco("BT-55E9B112");
            c3.setMensagemBanco("Pagamento confirmado com sucesso via CARTAO_DEBITO pelo Banco Tranquilo.");
            c3.setDataCompra(LocalDateTime.now().minusHours(1));
            c3.setDataConfirmacao(LocalDateTime.now().minusHours(1).plusSeconds(2));
            c3.setEmailEnviado(true);
            c3 = compraRepository.save(c3);
            emailNotificationService.enviarEmailConfirmacaoPagamento(c3);
        }
    }
}