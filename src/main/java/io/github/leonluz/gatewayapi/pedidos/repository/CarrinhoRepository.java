package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, String> {
    Optional<Carrinho> findByUsuario(Usuario usuario);
}
