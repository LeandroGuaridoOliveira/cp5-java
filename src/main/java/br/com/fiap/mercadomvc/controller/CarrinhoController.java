package br.com.fiap.mercadomvc.controller;

import br.com.fiap.mercadomvc.config.RabbitMQConfig;
import br.com.fiap.mercadomvc.dto.CompraRequestDto;
import br.com.fiap.mercadomvc.dto.ItemCompraDto;
import br.com.fiap.mercadomvc.model.Compra;
import br.com.fiap.mercadomvc.model.Produto;
import br.com.fiap.mercadomvc.model.Usuario;
import br.com.fiap.mercadomvc.service.PagamentoMensageriaService;
import br.com.fiap.mercadomvc.service.ProdutoService;
import br.com.fiap.mercadomvc.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    private static final String SESSION_CART_KEY = "CARRINHO_ITENS";

    private final ProdutoService produtoService;
    private final PagamentoMensageriaService pagamentoMensageriaService;
    private final UsuarioService usuarioService;

    @Autowired
    public CarrinhoController(ProdutoService produtoService,
                              PagamentoMensageriaService pagamentoMensageriaService,
                              UsuarioService usuarioService) {
        this.produtoService = produtoService;
        this.pagamentoMensageriaService = pagamentoMensageriaService;
        this.usuarioService = usuarioService;
    }

    @SuppressWarnings("unchecked")
    private List<ItemCompraDto> obterCarrinho(HttpSession session) {
        List<ItemCompraDto> itens = (List<ItemCompraDto>) session.getAttribute(SESSION_CART_KEY);
        if (itens == null) {
            itens = new ArrayList<>();
            session.setAttribute(SESSION_CART_KEY, itens);
        }
        return itens;
    }

    @GetMapping
    public String exibirCarrinho(HttpSession session, Principal principal, Model model) {
        List<ItemCompraDto> itens = obterCarrinho(session);
        double valorTotal = itens.stream()
                .mapToDouble(i -> (i.getPrecoUnitario() != null ? i.getPrecoUnitario() : 0.0) * i.getQuantidade())
                .sum();
        int quantidadeTotal = itens.stream()
                .mapToInt(ItemCompraDto::getQuantidade)
                .sum();

        String nomeSugestao = "";
        String emailSugestao = "";
        if (principal != null) {
            Optional<Usuario> userOpt = usuarioService.buscarPorUsername(principal.getName());
            if (userOpt.isPresent()) {
                nomeSugestao = userOpt.get().getNomeCompleto();
                emailSugestao = userOpt.get().getUsername() + "@mercadoexpress.com.br";
            }
        }

        model.addAttribute("itensCarrinho", itens);
        model.addAttribute("valorTotal", valorTotal);
        model.addAttribute("quantidadeTotal", quantidadeTotal);
        model.addAttribute("produtosDisponiveis", produtoService.listarTodos());
        model.addAttribute("nomeSugestao", nomeSugestao);
        model.addAttribute("emailSugestao", emailSugestao);
        model.addAttribute("exchangeName", RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO);
        model.addAttribute("queueReq", RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO);
        model.addAttribute("queueResp", RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA);

        return "carrinho/checkout";
    }

    @PostMapping("/adicionar")
    public String adicionarItem(@RequestParam("produtoId") Long produtoId,
                                @RequestParam(value = "quantidade", defaultValue = "1") Integer quantidade,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Optional<Produto> prodOpt = produtoService.buscarPorId(produtoId);
        if (prodOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado.");
            return "redirect:/carrinho";
        }

        Produto prod = prodOpt.get();
        int qtd = Math.max(1, quantidade);
        List<ItemCompraDto> itens = obterCarrinho(session);

        boolean encontrado = false;
        for (ItemCompraDto item : itens) {
            if (item.getProdutoId().equals(prod.getId())) {
                item.setQuantidade(item.getQuantidade() + qtd);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            itens.add(new ItemCompraDto(prod.getId(), prod.getNome(), qtd, prod.getPreco()));
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso",
                qtd + "x '" + prod.getNome() + "' adicionado(s) ao carrinho!");
        return "redirect:/carrinho";
    }

    @GetMapping("/adicionar-rapido/{id}")
    public String adicionarRapido(@PathVariable("id") Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        return adicionarItem(id, 1, session, redirectAttributes);
    }

    @GetMapping("/remover/{produtoId}")
    public String removerItem(@PathVariable("produtoId") Long produtoId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        List<ItemCompraDto> itens = obterCarrinho(session);
        itens.removeIf(i -> i.getProdutoId().equals(produtoId));
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Item removido do carrinho.");
        return "redirect:/carrinho";
    }

    @GetMapping("/limpar")
    public String limparCarrinho(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(SESSION_CART_KEY);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Carrinho esvaziado.");
        return "redirect:/carrinho";
    }

    @PostMapping("/finalizar")
    public String finalizarCompra(@RequestParam("nomeCliente") String nomeCliente,
                                  @RequestParam("emailCliente") String emailCliente,
                                  @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
                                  @RequestParam("metodoPagamento") String metodoPagamento,
                                  @RequestParam(value = "numeroCartao", required = false) String numeroCartao,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        List<ItemCompraDto> itens = obterCarrinho(session);
        if (itens.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Adicione pelo menos um produto ao carrinho antes de finalizar a compra.");
            return "redirect:/carrinho";
        }

        CompraRequestDto requestDto = new CompraRequestDto(
                nomeCliente,
                emailCliente,
                cpfCliente,
                metodoPagamento,
                numeroCartao,
                new ArrayList<>(itens)
        );

        Compra compra = pagamentoMensageriaService.realizarCompra(requestDto);

        // Aguarda brevemente caso o listener RabbitMQ esteja processando de forma assíncrona
        for (int i = 0; i < 10; i++) {
            Optional<Compra> atualizada = pagamentoMensageriaService.buscarPorId(compra.getId());
            if (atualizada.isPresent() && atualizada.get().getProtocoloBanco() != null) {
                compra = atualizada.get();
                break;
            }
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        session.removeAttribute(SESSION_CART_KEY);
        redirectAttributes.addFlashAttribute("compraRealizada", compra);
        redirectAttributes.addFlashAttribute("mensagemSucesso",
                "Compra enviada para a fila RabbitMQ e processada pelo Banco Tranquilo! Protocolo: " + compra.getProtocoloBanco());

        return "redirect:/carrinho/comprovante/" + compra.getCodigoTransacao();
    }

    @GetMapping("/comprovante/{codigoTransacao}")
    public String exibirComprovante(@PathVariable("codigoTransacao") String codigoTransacao,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Optional<Compra> compraOpt = pagamentoMensageriaService.buscarPorCodigoTransacao(codigoTransacao);
        if (compraOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Transação não encontrada.");
            return "redirect:/carrinho";
        }

        model.addAttribute("compra", compraOpt.get());
        model.addAttribute("exchangeName", RabbitMQConfig.EXCHANGE_BANCO_TRANQUILO);
        model.addAttribute("queueReq", RabbitMQConfig.QUEUE_PAGAMENTO_REQUISICAO);
        model.addAttribute("queueResp", RabbitMQConfig.QUEUE_PAGAMENTO_RESPOSTA);
        return "carrinho/comprovante";
    }
}
