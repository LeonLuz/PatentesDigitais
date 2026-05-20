    package io.github.leonluz.gatewayapi.pedidos.dto;

    import io.github.leonluz.gatewayapi.pedidos.model.StatusAquisicao;

    import java.util.List;

    public record AquisicaoRequestDTO(
            String idUsuario,
            StatusAquisicao statusAquisicao,
            List<ItemAquisicaoRequestDTO> itensAquisicao) {
    }
