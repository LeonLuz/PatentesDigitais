package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
