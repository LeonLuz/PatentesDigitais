package io.github.leonluz.gatewayapi.autenticacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "E-mail é obrigatório!")
        @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres!")
        String email,

        @NotBlank(message = "Senha é obrigatória!")
        @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres!")
        String senha
) {}