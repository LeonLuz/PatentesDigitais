package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> obterUsuarioPorEmail(String email) {
        return this.usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail e/ou senha inválidos."));

        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("E-mail e/ou senha inválidos.");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario obterUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public void deletarUsuarioPorId(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado para exclusão.");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> listarIdUsuarios() {
        return this.usuarioRepository.listarTodosIds();
    }
}