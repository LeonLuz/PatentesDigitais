package io.github.leonluz.gatewayapi.autenticacao.dto;

public record NITRequestDTO(
        String email,
        String senha,
        String telefone,
        String endereco,
        String cnpj,
        String razaoSocial
) {
    //depois adicionar as validacoes e exceptions
}
