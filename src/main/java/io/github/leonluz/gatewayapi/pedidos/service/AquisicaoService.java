package io.github.leonluz.gatewayapi.pedidos.service;

import io.github.leonluz.gatewayapi.pedidos.repository.AquisicaoRepository;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AquisicaoService {

    private final AquisicaoRepository aquisicaoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;

    public AquisicaoService(AquisicaoRepository aquisicaoRepository, CarrinhoRepository carrinhoRepository, PatenteRepository patenteRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.patenteRepository = patenteRepository;
    }

    @Transactional
    public String finalizarCheckout(String idUsuario) {
        // 1. Pega o carrinho da organização
        String idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        if (idCarrinho == null) {
            throw new IllegalStateException("Nenhum carrinho ativo encontrado para este usuário.");
        }

        // 2. Lista os UUIDs das patentes que estão no carrinho
        List<String> idsPatentes = carrinhoRepository.listarItensDoCarrinho(idCarrinho);
        if (idsPatentes.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio. Adicione patentes antes de finalizar.");
        }

        // 3. Cria o registro PAI da Aquisição e gera o UUID definitivo
        String idAquisicao = UUID.randomUUID().toString();
        aquisicaoRepository.criarAquisicao(idAquisicao, idUsuario);

        // 4. Processa cada patente da lista
        for (String idPatente : idsPatentes) {
            Patente patente = patenteRepository.findById(idPatente)
                    .orElseThrow(() -> new IllegalArgumentException("Patente não encontrada no banco."));

            // Validação de concorrência: Verifica se ninguém comprou a patente enquanto ela estava no carrinho!
            if (!patente.getStatus().name().equals("DISPONIVEL")) {
                throw new IllegalStateException("A patente " + patente.getTitulo() + " não está mais disponível para compra.");
            }

            // Move a patente para os itens da aquisição
            String idItemAquisicao = UUID.randomUUID().toString();
            aquisicaoRepository.adicionarItemAquisicao(idItemAquisicao, idAquisicao, idPatente);

            // Bloqueia a patente para que outras organizações não consigam comprar
            patente.setStatus(StatusPatente.EM_PROCESSO_DE_COMPRA);
            patenteRepository.save(patente);
        }

        // 5. Limpa a "lista de intenção" esvaziando o carrinho
        carrinhoRepository.esvaziarCarrinho(idCarrinho);

        return idAquisicao;
    }
}