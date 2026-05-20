package io.github.leonluz.gatewayapi.patentes.repository;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatenteRepository extends JpaRepository<Patente, String> {

}
