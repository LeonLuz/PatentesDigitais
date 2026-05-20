package io.github.leonluz.gatewayapi.pedidos.service;

import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, PatenteRepository patenteRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.patenteRepository = patenteRepository;
    }

    // Método auxiliar privado para buscar ou gerar o carrinho
    private String obterOuCriarCarrinho(String idUsuario) {
        String idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        
        if (idCarrinho == null) {
            idCarrinho = UUID.randomUUID().toString();
            carrinhoRepository.criarCarrinho(idCarrinho, idUsuario);
        }
        return idCarrinho;
    }

    @Transactional
    public void adicionarAoCarrinho(String idUsuario, String idPatente) {
        // 1. Valida a patente e seu status
        Patente patente = patenteRepository.findById(idPatente)
                .orElseThrow(() -> new IllegalArgumentException("Patente não localizada no sistema."));
        
        if (!patente.getStatus().name().equals("DISPONIVEL")) {
            throw new IllegalStateException("Esta patente não está disponível para negociação no momento.");
        }

        // 2. Garante que o usuário tem um carrinho ativo e pega o ID
        String idCarrinho = obterOuCriarCarrinho(idUsuario);

        // 3. Impede duplicação do mesmo item no carrinho
        if (carrinhoRepository.verificarItemExistente(idCarrinho, idPatente) > 0) {
            throw new IllegalStateException("Esta patente já consta no seu carrinho de compras.");
        }

        // 4. Gera o UUID do item e salva na tabela ITEM_CARRINHO
        String novoIdItem = UUID.randomUUID().toString();
        carrinhoRepository.adicionarItem(novoIdItem, idCarrinho, idPatente);
    }

    @Transactional
    public void removerDoCarrinho(String idUsuario, String idPatente) {
        String idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        
        if (idCarrinho == null || carrinhoRepository.verificarItemExistente(idCarrinho, idPatente) == 0) {
            throw new IllegalArgumentException("A patente não foi encontrada no carrinho desta organização.");
        }
        
        carrinhoRepository.removerItem(idCarrinho, idPatente);
    }
}