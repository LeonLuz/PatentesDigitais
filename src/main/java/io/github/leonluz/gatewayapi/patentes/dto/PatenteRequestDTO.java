package io.github.leonluz.gatewayapi.patentes.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PatenteRequestDTO(
        String titulo,
        String numDeposito,
        String resumo,
        String area,
        BigDecimal valor,
        String pesquisadores,
        String documento,
        String status, // Adicionado
        List<UUID> idsPesquisadoresAssociados
) {}
