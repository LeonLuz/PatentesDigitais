package io.github.leonluz.gatewayapi.pedidos.service;

<<<<<<< HEAD
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
=======
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.pedidos.dto.CarrinhoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.model.ItemCarrinho;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
>>>>>>> main
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
<<<<<<< HEAD
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
=======
    private final UsuarioRepository usuarioRepository;
    private final PatenteRepository patenteRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository,
                           UsuarioRepository usuarioRepository,
                           PatenteRepository patenteRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
        this.patenteRepository = patenteRepository;
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

    public Carrinho buscarPorUsuario(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Nenhum carrinho ativo encontrado para este usuário"));
>>>>>>> main
    }
}