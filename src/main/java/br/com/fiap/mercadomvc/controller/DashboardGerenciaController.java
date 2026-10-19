package br.com.fiap.mercadomvc.controller;

import br.com.fiap.mercadomvc.config.RabbitMQConfig;
import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.EmailLog;
import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.StatusPagamento;
import br.com.fiap.mercadomvc.service.EmailNotificationService;
import br.com.fiap.mercadomvc.service.PagamentoMensageriaService;
import br.com.fiap.mercadomvc.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard-gerencia")
public class DashboardGerenciaController {

    private final PagamentoMensageriaService pagamentoMensageriaService;
    private final EmailNotificationService emailNotificationService;
    private final ProdutoService produtoService;

    @Autowired
    public DashboardGerenciaController(PagamentoMensageriaService pagamentoMensageriaService,
                                       EmailNotificationService emailNotificationService,
                                       ProdutoService produtoService) {
        this.pagamentoMensageriaService = pagamentoMensageriaService;
        this.emailNotificationService = emailNotificationService;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Compra> compras = pagamentoMensageriaService.listarTodasCompras();
        List<EmailLog> emails = emailNotificationService.listarTodosEmails();
        List<Produto> produtos = produtoService.listarTodos();

        List<Compra> comprasAprovadas = compras.stream()
                .filter(c -> c.getStatusPagamento() == StatusPagamento.APROVADO)
                .toList();

        long totalCompras = compras.size();
        long totalAprovadas = comprasAprovadas.size();
        long totalRecusadas = compras.stream()
                .filter(c -> c.getStatusPagamento() == StatusPagamento.RECUSADO)
                .count();

        double receitaTotal = comprasAprovadas.stream()
                .mapToDouble(c -> c.getValorTotal() != null ? c.getValorTotal() : 0.0)
                .sum();

        double lucroTotal = comprasAprovadas.stream()
                .mapToDouble(c -> c.getLucroEstimado() != null ? c.getLucroEstimado() : 0.0)
                .sum();

        int estoqueTotalItens = produtos.stream()
                .mapToInt(Produto::getEstoque)
                .sum();

        double valorTotalEstoque = produtos.stream()
                .mapToDouble(p -> (p.getPreco() != null ? p.getPreco() : 0.0) * p.getEstoque())
                .sum();

        // Dados para Gráfico 1: Vendas por Método de Pagamento
        Map<String, Double> vendasPorMetodo = new LinkedHashMap<>();
        vendasPorMetodo.put("PIX", 0.0);
        vendasPorMetodo.put("CARTAO_CREDITO", 0.0);
        vendasPorMetodo.put("CARTAO_DEBITO", 0.0);
        vendasPorMetodo.put("BOLETO", 0.0);

        for (Compra c : comprasAprovadas) {
            String metodo = c.getMetodoPagamento() != null ? c.getMetodoPagamento().toUpperCase() : "OUTROS";
            vendasPorMetodo.put(metodo, vendasPorMetodo.getOrDefault(metodo, 0.0) + c.getValorTotal());
        }

        // Dados para Gráfico 2: Estoque por Produto
        List<String> nomesProdutos = produtos.stream().map(Produto::getNome).toList();
        List<Integer> estoquesProdutos = produtos.stream().map(Produto::getEstoque).toList();

        // Dados para Gráfico 3: Estoque e Valor por Setor
        Map<String, Integer> estoquePorSetor = new LinkedHashMap<>();
        for (Produto p : produtos) {
            String setor = p.getSetor() != null ? p.getSetor() : "Geral";
            estoquePorSetor.put(setor, estoquePorSetor.getOrDefault(setor, 0) + p.getEstoque());
        }

        // Dados para Gráfico 4: Comparativo Vendas x Lucro por Transação Aprovada
        List<String> labelsTransacoes = new ArrayList<>();
        List<Double> valoresVendaTransacao = new ArrayList<>();
        List<Double> valoresLucroTransacao = new ArrayList<>();
        for (int i = Math.min(comprasAprovadas.size(), 8) - 1; i >= 0; i--) {
            Compra c = comprasAprovadas.get(i);
            labelsTransacoes.add(c.getCodigoTransacao());
            valoresVendaTransacao.add(c.getValorTotal());
            valoresLucroTransacao.add(c.getLucroEstimado());
        }

        model.addAttribute("compras", compras);
        model.addAttribute("emails", emails);
        model.addAttribute("produtos", produtos);

        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("totalAprovadas", totalAprovadas);
        model.addAttribute("totalRecusadas", totalRecusadas);
        model.addAttribute("receitaTotal", receitaTotal);
        model.addAttribute("lucroTotal", lucroTotal);
        model.addAttribute("estoqueTotalItens", estoqueTotalItens);
        model.addAttribute("valorTotalEstoque", valorTotalEstoque);
        model.addAttribute("totalEmails", emails.size());

        model.addAttribute("metodosLabels", new ArrayList<>(vendasPorMetodo.keySet()));
        model.addAttribute("metodosValores", new ArrayList<>(vendasPorMetodo.values()));
        model.addAttribute("nomesProdutos", nomesProdutos);
        model.addAttribute("estoquesProdutos", estoquesProdutos);
        model.addAttribute("setoresLabels", new ArrayList<>(estoquePorSetor.keySet()));
        model.addAttribute("setoresEstoque", new ArrayList<>(estoquePorSetor.values()));
        model.addAttribute("labelsTransacoes", labelsTransacoes);
        model.addAttribute("valoresVendaTransacao", valoresVendaTransacao);
        model.addAttribute("valoresLucroTransacao", valoresLucroTransacao);

        model.addAttribute("exchangeName", RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO);
        model.addAttribute("queueReq", RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO);
        model.addAttribute("queueResp", RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA);

        return "dashboard/gerencia";
    }
}
