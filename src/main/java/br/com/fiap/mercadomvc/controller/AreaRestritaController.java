package br.com.fiap.mercadomvc.controller;

import br.com.fiap.mercadomvc.model.Usuario;
import br.com.fiap.mercadomvc.service.ProdutoService;
import br.com.fiap.mercadomvc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/area-restrita")
public class AreaRestritaController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @Autowired
    public AreaRestritaController(ProdutoService produtoService, UsuarioService usuarioService) {
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String index(Principal principal, Authentication authentication, Model model) {
        if (principal != null) {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(principal.getName());
            usuarioOpt.ifPresent(usuario -> model.addAttribute("usuarioLogado", usuario));
        }

        model.addAttribute("totalProdutos", produtoService.totalProdutos());
        model.addAttribute("precoMedio", produtoService.precoMedio());
        model.addAttribute("totalUsuarios", usuarioService.totalUsuarios());
        model.addAttribute("produtosDestaque", produtoService.listarTodos().stream().limit(5).toList());

        return "area-restrita";
    }
}
