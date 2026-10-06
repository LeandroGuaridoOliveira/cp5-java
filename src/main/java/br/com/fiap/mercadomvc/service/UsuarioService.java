package br.com.fiap.mercadomvc.service;

import br.com.fiap.mercadomvc.dto.UsuarioRegistroDto;
import br.com.fiap.mercadomvc.model.Role;
import br.com.fiap.mercadomvc.model.Usuario;
import br.com.fiap.mercadomvc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrar(UsuarioRegistroDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new IllegalArgumentException("A confirmação de senha não confere com a senha informada.");
        }

        if (usuarioRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("O nome de usuário '" + dto.getUsername().trim() + "' já está em uso.");
        }

        Role perfil = dto.getRole() != null ? dto.getRole() : Role.ROLE_USER;

        Usuario novoUsuario = Usuario.builder()
                .nomeCompleto(dto.getNomeCompleto().trim())
                .username(dto.getUsername().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(perfil)
                .ativo(true)
                .build();

        return usuarioRepository.save(novoUsuario);
    }

    @Transactional
    public Usuario salvarUsuarioSeeder(String nomeCompleto, String username, String senhaPlana, Role role) {
        if (usuarioRepository.existsByUsername(username.toLowerCase())) {
            return usuarioRepository.findByUsername(username.toLowerCase()).get();
        }

        Usuario usuario = Usuario.builder()
                .nomeCompleto(nomeCompleto)
                .username(username.toLowerCase())
                .password(passwordEncoder.encode(senhaPlana))
                .role(role)
                .ativo(true)
                .build();

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username != null ? username.toLowerCase() : null);
    }

    public long totalUsuarios() {
        return usuarioRepository.count();
    }
}
