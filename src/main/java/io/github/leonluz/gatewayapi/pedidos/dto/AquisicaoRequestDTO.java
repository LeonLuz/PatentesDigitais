    package io.github.leonluz.gatewayapi.pedidos.dto;

    import io.github.leonluz.gatewayapi.pedidos.model.StatusAquisicao;

    import java.util.List;
    import java.util.UUID;

    public record AquisicaoRequestDTO(
            UUID idUsuario,
            StatusAquisicao statusAquisicao,
            List<ItemAquisicaoRequestDTO> itensAquisicao) {
    }
