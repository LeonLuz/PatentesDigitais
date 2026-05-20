package io.github.leonluz.gatewayapi.autenticacao.dto;

public record OrganizacaoRequestDTO(
        String email,
        String senha,
        String telefone,
        String endereco,
        String cnpj,
        String razaoSocial
) {
    //validacoes dps
}
