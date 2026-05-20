package io.github.leonluz.gatewayapi.patentes.dto;

import java.util.List;

public record PatenteRequestDTO(
        String idTitular,
        String titulo,
        String numDeposito,
        String resumo,
        String area,
        Double valor,
        String pesquisadores,
        String documento,
        List<String> idsPesquisadoresAssociados
) {}