package io.github.leonluz.gatewayapi.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PesquisadorRequestDTO(
        @NotBlank(message = "E-mail é obrigatório!")
        @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres!")
        String email,

        @NotBlank(message = "Senha é obrigatória!")
        @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres!")
        String senha,

        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres!")
        String telefone,

        @Size(max = 250, message = "Endereço deve ter no máximo 250 caracteres!")
        String endereco,

        @NotBlank(message = "CPF é obrigatório!")
        @Size(min = 11, max = 11, message = "CPF deve ter exatamente 11 dígitos!")
        @Pattern(regexp = "\\d+", message = "CPF deve conter somente números!")
        String cpf,

        @NotBlank(message = "Nome é obrigatório!")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres!")
        String nome
) {
}
