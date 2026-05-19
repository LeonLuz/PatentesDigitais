package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrganizacaoRepository extends JpaRepository<Organizacao, String> {
    Organizacao findByRazaoSocial(String razaoSocial);
}
