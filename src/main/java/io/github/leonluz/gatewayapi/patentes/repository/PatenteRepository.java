package io.github.leonluz.gatewayapi.patentes.repository;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PatenteRepository extends JpaRepository<Patente, UUID> {
}
