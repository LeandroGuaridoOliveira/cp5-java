package br.com.fiap.mercadomvc.config;

import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.Role;
import br.com.fiap.mercadomvc.repository.ProdutoRepository;
import br.com.fiap.mercadomvc.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;

    public DatabaseSeeder(ProdutoRepository produtoRepository, UsuarioService usuarioService) {
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
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
                new Produto("Sabão em Pó Omo Lavagem Perfeita", "Produto de Limpeza", "Limpeza", "1.6kg", 24.90),
                new Produto("Detergente Líquido Neutro Ypê", "Produto de Limpeza", "Limpeza", "500ml", 2.89),
                new Produto("Kit Meia Cano Médio Lupo (3 pares)", "Vestuário", "Meias e Acessórios", "39-43", 29.90),
                new Produto("Maçã Fuji Selecionada", "Hortifruti", "Frutas e Verduras", "1kg", 9.80),
                new Produto("Banana Prata Premium", "Hortifruti", "Frutas e Verduras", "1kg", 6.50),
                new Produto("Carrinho de Controle Remoto Speed", "Brinquedo", "Brinquedos", "Único", 89.90),
                new Produto("Jogo de Tabuleiro Clássico", "Brinquedo", "Brinquedos", "Médio", 49.99),
                new Produto("Desinfetante Pinho Sol Original", "Produto de Limpeza", "Limpeza", "1L", 11.40)
            ));
        }
    }
}