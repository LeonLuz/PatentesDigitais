package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.NitUpdateDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.NITRepository;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NITService {
    private final NITRepository nitRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final UsuarioRepository usuarioRepository;

    public NITService(NITRepository nitRepository, CarrinhoRepository carrinhoRepository, UsuarioRepository usuarioRepository) {
        this.nitRepository = nitRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public NIT salvarNit(NITRequestDTO dto) {
        NIT nit = new NIT(dto);

        nit.setIdUsuario(UUID.randomUUID());
        nit.setTipoPerfil(TipoPerfil.NIT);
        nit.setStatusAtivo(true);
        nit.setStatusAuth(true);

        NIT nitSalvo = nitRepository.save(nit);

        Carrinho novoCarrinho = new Carrinho(nitSalvo);
        carrinhoRepository.save(novoCarrinho);

        return nitSalvo;
    }

    public NIT atualizarNit(UUID id, NitUpdateDTO dto) {
        NIT nitExistente = nitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NIT não encontrado"));

        nitExistente.setEmail(dto.email());
        nitExistente.setTelefone(dto.telefone());
        nitExistente.setEndereco(dto.endereco());
        nitExistente.setRazaoSocial(dto.razaoSocial());

        return nitRepository.save(nitExistente);
    }

    @Transactional
    public void adicionarAssociado(UUID idInstituicao, UUID idUsuarioAssociado) {

        if (!usuarioRepository.existsById(idInstituicao)) {
            throw new IllegalArgumentException("Instituição não encontrada com o ID fornecido.");
        }

        if (!usuarioRepository.existsById(idUsuarioAssociado)) {
            throw new IllegalArgumentException("Usuário a ser associado não encontrado.");
        }

        if (usuarioRepository.existsVinculo(idInstituicao, idUsuarioAssociado) > 0) {
            throw new IllegalStateException("Este usuário já se encontra associado a esta instituição.");
        }

        usuarioRepository.adicionarVinculo(idInstituicao, idUsuarioAssociado);
    }

    @Transactional
    public void excluirAssociado(UUID idInstituicao, UUID idUsuarioAssociado) {

        if (usuarioRepository.existsVinculo(idInstituicao, idUsuarioAssociado) == 0) {
            throw new IllegalArgumentException("Não foi encontrado nenhum vínculo entre a instituição e o usuário informados.");
        }

        // 2. Remover o vínculo da tabela intermediária
        usuarioRepository.removerVinculo(idInstituicao, idUsuarioAssociado);
    }

    public NIT findByRazaoSocial(String razaoSocial) {
        return nitRepository.findByRazaoSocial(razaoSocial);
    }
}
