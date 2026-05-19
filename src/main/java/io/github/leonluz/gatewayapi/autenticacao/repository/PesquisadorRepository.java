package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesquisadorRepository extends JpaRepository<Pesquisador, String> {
    Pesquisador findByNome(String nome);
}
