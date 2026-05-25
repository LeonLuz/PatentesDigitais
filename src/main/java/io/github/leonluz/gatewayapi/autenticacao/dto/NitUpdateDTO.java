package io.github.leonluz.gatewayapi.autenticacao.dto;

public record NitUpdateDTO(
        String email,
        String telefone,
        String endereco,
        String razaoSocial
) {}