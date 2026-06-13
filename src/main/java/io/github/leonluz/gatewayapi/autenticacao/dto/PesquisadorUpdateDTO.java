package io.github.leonluz.gatewayapi.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PesquisadorUpdateDTO(
        @NotBlank(message = "E-mail é obrigatório!")
        @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres!")
        String email,

        @Size(min = 10, max = 20, message = "Telefone deve ter entre 10 e 20 caracteres!")
        @Pattern(regexp = "^[0-9]*$", message = "Telefone deve conter somente números!")
        String telefone,

        @Size(max = 250, message = "Endereço deve ter no máximo 250 caracteres!")
        String endereco,

        @NotBlank(message = "Nome é obrigatório!")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres!")
        String nome,

        Boolean disponibilidadeConsultoria
) {}