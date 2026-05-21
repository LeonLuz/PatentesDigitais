package io.github.leonluz.gatewayapi.pedidos.service;

<<<<<<< HEAD
import io.github.leonluz.gatewayapi.pedidos.repository.AquisicaoRepository;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
=======
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.pedidos.dto.AquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.dto.ItemAquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.model.ItemAquisicao;
import io.github.leonluz.gatewayapi.pedidos.repository.AquisicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> main
import java.util.UUID;

@Service
public class AquisicaoService {

    private final AquisicaoRepository aquisicaoRepository;
<<<<<<< HEAD
    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;

    public AquisicaoService(AquisicaoRepository aquisicaoRepository, CarrinhoRepository carrinhoRepository, PatenteRepository patenteRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.carrinhoRepository = carrinhoRepository;
=======
    private final UsuarioRepository usuarioRepository;
    private final PatenteRepository patenteRepository;

    public AquisicaoService(AquisicaoRepository aquisicaoRepository,
                            UsuarioRepository usuarioRepository,
                            PatenteRepository patenteRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.usuarioRepository = usuarioRepository;
>>>>>>> main
        this.patenteRepository = patenteRepository;
    }

    @Transactional
<<<<<<< HEAD
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
=======
    public Aquisicao criarAquisicao(AquisicaoRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado"));

        Aquisicao aquisicao = new Aquisicao(usuario);

        if (dto.itensAquisicao() != null) {
            for (ItemAquisicaoRequestDTO itemDto : dto.itensAquisicao()) {
                Patente patente = patenteRepository.findById(itemDto.idPatente())
                        .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

                ItemAquisicao item = new ItemAquisicao();
                item.setIdItem(UUID.randomUUID().toString());
                item.setIdAquisicao(aquisicao);
                item.setPatente(patente);
                item.setTipoAquisicao(itemDto.tipoAquisicao());
                item.setFimLicenca(itemDto.fimLicenca());

                aquisicao.getItens().add(item);
            }
        }

        return aquisicaoRepository.save(aquisicao);
    }

    public Aquisicao buscarPorId(String id) {
        return aquisicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aquisição não encontrada"));
>>>>>>> main
    }
}