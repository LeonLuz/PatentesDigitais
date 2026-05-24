package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PesquisadorRepository extends JpaRepository<Pesquisador, UUID> {
    Pesquisador findByNome(String nome);
}
