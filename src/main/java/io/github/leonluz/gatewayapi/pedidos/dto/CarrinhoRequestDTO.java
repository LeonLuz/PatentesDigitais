package io.github.leonluz.gatewayapi.pedidos.dto;

import java.util.UUID;

public record CarrinhoRequestDTO(
        UUID idUsuario,
        UUID idPatente
) {}