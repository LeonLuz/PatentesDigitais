package io.github.leonluz.gatewayapi.autenticacao.dto;

public record OrganizacaoUpdateDTO(
        String email,
        String telefone,
        String endereco,
        String razaoSocial,
        String cnpj
) {}