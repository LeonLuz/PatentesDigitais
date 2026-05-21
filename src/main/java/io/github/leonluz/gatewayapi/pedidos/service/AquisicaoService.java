package io.github.leonluz.gatewayapi.pedidos.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import io.github.leonluz.gatewayapi.pedidos.dto.AquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.dto.ItemAquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.model.ItemAquisicao;
import io.github.leonluz.gatewayapi.pedidos.repository.AquisicaoRepository;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AquisicaoService {

    private final AquisicaoRepository aquisicaoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PatenteRepository patenteRepository;
    private final UsuarioRepository usuarioRepository;


    public AquisicaoService(AquisicaoRepository aquisicaoRepository,
                            CarrinhoRepository carrinhoRepository,
                            PatenteRepository patenteRepository,
                            UsuarioRepository usuarioRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.patenteRepository = patenteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public String finalizarCheckout(String idUsuario) {

        String idCarrinho = carrinhoRepository.buscarIdCarrinhoPorUsuario(idUsuario);
        if (idCarrinho == null) {
            throw new IllegalStateException("Nenhum carrinho ativo encontrado para este usuário.");
        }

        List<String> idsPatentes = carrinhoRepository.listarItensDoCarrinho(idCarrinho);
        if (idsPatentes.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio. Adicione patentes antes de finalizar.");
        }

        String idAquisicao = UUID.randomUUID().toString();
        aquisicaoRepository.criarAquisicao(idAquisicao, idUsuario);

        for (String idPatente : idsPatentes) {
            Patente patente = patenteRepository.findById(idPatente)
                    .orElseThrow(() -> new IllegalArgumentException("Patente não encontrada no banco."));

            // Validação de concorrência
            if (!"DISPONIVEL".equals(patente.getStatus().name())) {
                throw new IllegalStateException("A patente " + patente.getTitulo() + " não está mais disponível para compra.");
            }


            String idItemAquisicao = UUID.randomUUID().toString();
            aquisicaoRepository.adicionarItemAquisicao(idItemAquisicao, idAquisicao, idPatente);

            patente.setStatus(StatusPatente.EM_PROCESSO_DE_COMPRA);
            patenteRepository.save(patente);
        }

        // Limpa a "lista de intenção" esvaziando o carrinho
        carrinhoRepository.esvaziarCarrinho(idCarrinho);

        return idAquisicao;
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public Aquisicao buscarPorId(String id) {
        return aquisicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aquisição não encontrada"));
    }
}