package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AquisicaoRepository extends JpaRepository<Aquisicao, String> {
}
