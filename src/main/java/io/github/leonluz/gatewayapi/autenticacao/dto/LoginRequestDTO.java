package io.github.leonluz.gatewayapi.autenticacao.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {}