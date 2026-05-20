package io.github.leonluz.gatewayapi.pedidos.dto;

public record CarrinhoRequestDTO(
        String idUsuario,
        String idPatente
) {}