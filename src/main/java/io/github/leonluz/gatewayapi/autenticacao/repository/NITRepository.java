package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NITRepository extends JpaRepository<NIT, String> {
    NIT findByRazaoSocial(String razaoSocial);
}
