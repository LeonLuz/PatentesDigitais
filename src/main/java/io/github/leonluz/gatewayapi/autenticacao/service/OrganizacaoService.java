package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.OrganizacaoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizacaoService {
    private OrganizacaoRepository organizacaoRepository;

    public OrganizacaoService(OrganizacaoRepository organizacaoRepository) {
        this.organizacaoRepository = organizacaoRepository;
    }

    public Organizacao salvarOrganizacao(Organizacao organizacao) {
        System.out.println("Usuário nit recebido: " + organizacao);

        var id = UUID.randomUUID().toString();
        organizacao.setIdUsuario(id);
        organizacao.setTipoPerfil(TipoPerfil.ORGANIZACAO);

        return organizacaoRepository.save(organizacao);
    }

    public Organizacao atualizarOrganizacao(String id, Organizacao organizacao) {
        organizacao.setIdUsuario(id);
        return organizacaoRepository.save(organizacao);
    }

    public Organizacao findByRazaoSocial(String razaoSocial) {
        return organizacaoRepository.findByRazaoSocial(razaoSocial);
    }
}
