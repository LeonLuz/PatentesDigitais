package io.github.leonluz.gatewayapi.autenticacao.dto;

public record PesquisadorUpdateDTO(
        String email,
        String telefone,
        String endereco,
        String nome,
        String cpf,
        Boolean disponibilidadeConsultoria
) {}