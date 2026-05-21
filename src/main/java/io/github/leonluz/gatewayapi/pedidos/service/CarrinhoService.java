package io.github.leonluz.gatewayapi.pedidos.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.pedidos.dto.CarrinhoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.model.ItemCarrinho;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;
    private final UsuarioRepository usuarioRepository;


    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           PatenteRepository patenteRepository,
                           UsuarioRepository usuarioRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.patenteRepository = patenteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void adicionarAoCarrinho(String idUsuario, String idPatente) {

        Patente patente = patenteRepository.findById(idPatente)
                .orElseThrow(() -> new IllegalArgumentException("Patente não localizada no sistema."));

        if (!"DISPONIVEL".equals(patente.getStatus().name())) {
            throw new IllegalStateException("Esta patente não está disponível para negociação no momento.");
        }

        String idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        if (idCarrinho == null) {
            throw new IllegalStateException("Carrinho não encontrado para este usuário.");
        }

        if (carrinhoRepository.verificarItemExistente(idCarrinho, idPatente) > 0) {
            throw new IllegalStateException("Esta patente já consta no seu carrinho de compras.");
        }

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

    @Transactional
    public Carrinho adicionarItem(CarrinhoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Patente patente = patenteRepository.findById(dto.idPatente())
                .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para este usuário."));

        boolean patenteJaAdicionada = carrinho.getItens().stream()
                .anyMatch(item -> item.getIdPatente().getId().equals(dto.idPatente()));

        if (patenteJaAdicionada) {
            throw new RuntimeException("Esta patente já foi adicionada ao seu carrinho!");
        }

        ItemCarrinho novoItem = new ItemCarrinho();
        novoItem.setIdItem(UUID.randomUUID().toString());
        novoItem.setIdCarrinho(carrinho);
        novoItem.setIdPatente(patente);

        carrinho.getItens().add(novoItem);

        return carrinhoRepository.save(carrinho);
    }

    @Transactional(readOnly = true)
    public Carrinho buscarPorUsuario(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Nenhum carrinho ativo encontrado para este usuário"));
    }
}