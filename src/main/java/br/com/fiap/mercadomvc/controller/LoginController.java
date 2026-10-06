package br.com.fiap.mercadomvc.controller;

import br.com.fiap.mercadomvc.dto.UsuarioRegistroDto;
import br.com.fiap.mercadomvc.model.Role;
import br.com.fiap.mercadomvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    @Autowired
    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("erroMensagem", "Usuário ou senha inválidos. Tente novamente.");
        }
        if (logout != null) {
            model.addAttribute("sucessoMensagem", "Você foi desconectado com sucesso.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String exibirRegistro(Model model) {
        if (!model.containsAttribute("usuarioDto")) {
            UsuarioRegistroDto dto = new UsuarioRegistroDto();
            dto.setRole(Role.ROLE_USER);
            model.addAttribute("usuarioDto", dto);
        }
        model.addAttribute("roles", Role.values());
        return "registro";
    }

    @PostMapping("/registro")
    public String processarRegistro(
            @Valid @ModelAttribute("usuarioDto") UsuarioRegistroDto usuarioDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!usuarioDto.getPassword().isEmpty() && !usuarioDto.getPassword().equals(usuarioDto.getConfirmarPassword())) {
            bindingResult.rejectValue("confirmarPassword", "error.usuarioDto", "A confirmação de senha não confere.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "registro";
        }

        try {
            usuarioService.registrar(usuarioDto);
            redirectAttributes.addFlashAttribute("sucessoMensagem", "Cadastro realizado com sucesso! Faça seu login para acessar o sistema.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erroMensagem", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "registro";
        }
    }
}
