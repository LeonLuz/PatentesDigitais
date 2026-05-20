package io.github.leonluz.gatewayapi.pedidos.dto;

import io.github.leonluz.gatewayapi.pedidos.model.TipoAquisicao;

import java.time.LocalDate;

public record ItemAquisicaoRequestDTO(
        String idAquisicao,
        String idPatente,
        TipoAquisicao tipoAquisicao,
        LocalDate fimLicenca
) {}