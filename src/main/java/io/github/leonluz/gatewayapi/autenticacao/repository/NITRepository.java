package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NITRepository extends JpaRepository<NIT, UUID> {
    NIT findByRazaoSocial(String razaoSocial);
}
