package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoUpdateDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.OrganizacaoRepository;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizacaoService {
    private final OrganizacaoRepository organizacaoRepository;
    private final CarrinhoRepository carrinhoRepository;

    public OrganizacaoService(OrganizacaoRepository organizacaoRepository, CarrinhoRepository carrinhoRepository) {
        this.organizacaoRepository = organizacaoRepository;
        this.carrinhoRepository = carrinhoRepository;
    }

    @Transactional
    public Organizacao salvarOrganizacao(OrganizacaoRequestDTO dto) {

        Organizacao organizacao = new Organizacao(dto);

        organizacao.setIdUsuario(UUID.randomUUID());
        organizacao.setTipoPerfil(TipoPerfil.ORGANIZACAO);
        organizacao.setStatusAtivo(true);
        organizacao.setStatusAuth(true);

        Organizacao organizacaoSalva = organizacaoRepository.save(organizacao);

        Carrinho novoCarrinho = new Carrinho(organizacaoSalva);
        carrinhoRepository.save(novoCarrinho);

        return organizacaoSalva;
    }

    @Transactional
    public Organizacao atualizarOrganizacao(UUID id, OrganizacaoUpdateDTO dto) {
        Organizacao orgExistente = organizacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organização não encontrada"));

        orgExistente.setEmail(dto.email());
        orgExistente.setTelefone(dto.telefone());
        orgExistente.setEndereco(dto.endereco());
        orgExistente.setRazaoSocial(dto.razaoSocial());

        return organizacaoRepository.save(orgExistente);
    }

    public Organizacao findByRazaoSocial(String razaoSocial) {
        return organizacaoRepository.findByRazaoSocial(razaoSocial);
    }
}
