package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.pedidos.model.ItemAquisicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemAquisicaoRepository extends JpaRepository<ItemAquisicao, UUID> {
}
