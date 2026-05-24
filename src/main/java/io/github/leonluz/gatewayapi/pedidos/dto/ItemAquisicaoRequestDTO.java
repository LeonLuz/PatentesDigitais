package io.github.leonluz.gatewayapi.pedidos.dto;

import io.github.leonluz.gatewayapi.pedidos.model.TipoAquisicao;

import java.time.LocalDate;
import java.util.UUID;

public record ItemAquisicaoRequestDTO(
        UUID idAquisicao,
        UUID idPatente,
        TipoAquisicao tipoAquisicao,
        LocalDate fimLicenca
) {}