package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface OrganizacaoRepository extends JpaRepository<Organizacao, UUID> {
    Organizacao findByRazaoSocial(String razaoSocial);
}
