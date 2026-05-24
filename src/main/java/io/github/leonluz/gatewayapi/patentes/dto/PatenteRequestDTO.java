package io.github.leonluz.gatewayapi.patentes.dto;

import java.util.List;
import java.util.UUID;

public record PatenteRequestDTO(
        UUID idTitular,
        String titulo,
        String numDeposito,
        String resumo,
        String area,
        Double valor,
        String pesquisadores,
        String documento,
        List<UUID> idsPesquisadoresAssociados
) {}