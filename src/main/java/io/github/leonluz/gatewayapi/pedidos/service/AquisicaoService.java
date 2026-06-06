package io.github.leonluz.gatewayapi.pedidos.service;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.model.ItemAquisicao;
import io.github.leonluz.gatewayapi.pedidos.model.StatusAquisicao;
import io.github.leonluz.gatewayapi.pedidos.repository.AquisicaoRepository;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AquisicaoService {

    private final AquisicaoRepository aquisicaoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;

    public AquisicaoService(AquisicaoRepository aquisicaoRepository,
                            CarrinhoRepository carrinhoRepository,
                            PatenteRepository patenteRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.patenteRepository = patenteRepository;
    }

    @Transactional
    public UUID finalizarCheckout(UUID idUsuario) {

        UUID idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        if (idCarrinho == null) {
            throw new IllegalStateException("Nenhum carrinho ativo encontrado para este usuário.");
        }

        List<UUID> idsPatentes = carrinhoRepository.listarItensDoCarrinho(idCarrinho);
        if (idsPatentes.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio. Adicione patentes antes de finalizar.");
        }

        // Ordenar os ids para evitar deadlock
        Collections.sort(idsPatentes);

        UUID idAquisicao = UUID.randomUUID();
        aquisicaoRepository.criarAquisicao(idAquisicao, idUsuario);

        // Update de todas as patentes de uma vez
        int totalAtualizado = patenteRepository.atualizarStatusEmMassa(idsPatentes, StatusPatente.EM_PROCESSO_DE_COMPRA);

        if (totalAtualizado != idsPatentes.size()) {
            throw new IllegalStateException("Uma ou mais patentes escolhidas não estão mais disponíveis para compra.");
        }

        for (UUID idPatente : idsPatentes) {
            UUID idItemAquisicao = UUID.randomUUID();
            aquisicaoRepository.adicionarItemAquisicao(idItemAquisicao, idAquisicao, idPatente);
        }

        // Limpa a "lista de intenção" esvaziando o carrinho
        carrinhoRepository.esvaziarCarrinho(idCarrinho);

        return idAquisicao;
    }

    @Transactional
    public void cancelarAquisicao(UUID idAquisicao) {
        Aquisicao aquisicao = aquisicaoRepository.findById(idAquisicao)
                .orElseThrow(() -> new RuntimeException("Aquisição não encontrada"));

        // Altera o status da aquisição
        aquisicao.setStatusAquisicao(StatusAquisicao.CANCELADA);
        aquisicaoRepository.save(aquisicao);

        // Libera as patentes atreladas aos itens
        for (ItemAquisicao item : aquisicao.getItens()) {
            Patente patente = item.getPatente();
            patente.setStatus(StatusPatente.DISPONIVEL);
            patenteRepository.save(patente);
        }
    }

    @Transactional(readOnly = true)
    public Aquisicao buscarPorId(UUID id) {
        return aquisicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aquisição não encontrada"));
    }
}