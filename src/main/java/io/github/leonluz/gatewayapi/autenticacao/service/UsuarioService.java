package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obterUsuarioPorId(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deletarUsuarioPorId(String id) {
        usuarioRepository.deleteById(id);
    }

}
