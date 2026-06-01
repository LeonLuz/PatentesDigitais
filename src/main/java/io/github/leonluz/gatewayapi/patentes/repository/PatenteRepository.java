package io.github.leonluz.gatewayapi.patentes.repository;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatenteRepository extends JpaRepository<Patente, UUID> {
    @Query(value = "SELECT BIN_TO_UUID(id_patente) FROM PATENTE", nativeQuery = true)
    List<String> listarTodosIds();
}
