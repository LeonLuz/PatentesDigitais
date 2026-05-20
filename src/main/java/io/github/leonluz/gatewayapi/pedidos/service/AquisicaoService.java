package io.github.leonluz.gatewayapi.pedidos.service;

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

import java.util.UUID;

@Service
public class AquisicaoService {

    private final AquisicaoRepository aquisicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PatenteRepository patenteRepository;

    public AquisicaoService(AquisicaoRepository aquisicaoRepository,
                            UsuarioRepository usuarioRepository,
                            PatenteRepository patenteRepository) {
        this.aquisicaoRepository = aquisicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.patenteRepository = patenteRepository;
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

    public Aquisicao buscarPorId(String id) {
        return aquisicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aquisição não encontrada"));
    }
}