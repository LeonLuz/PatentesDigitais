package io.github.leonluz.gatewayapi.autenticacao.dto;

public record PesquisadorRequestDTO(
        String email,
        String senha,
        String telefone,
        String endereco,
        String cpf,
        String nome,
        Boolean disponibilidadeConsultoria
) {
    //verificacoes dps
}
