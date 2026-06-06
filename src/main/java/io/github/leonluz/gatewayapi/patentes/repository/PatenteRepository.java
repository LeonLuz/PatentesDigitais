package io.github.leonluz.gatewayapi.patentes.repository;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Repository
public interface PatenteRepository extends JpaRepository<Patente, UUID> {

    @Query(value = "SELECT BIN_TO_UUID(id_patente) FROM PATENTE", nativeQuery = true)
    List<String> listarTodosIds();

    @Query("SELECT p FROM Patente p LEFT JOIN FETCH p.idTitular")
    List<Patente> buscarTodasParaVitrine();

    @Query(value = "SELECT p FROM Patente p LEFT JOIN FETCH p.idTitular",
            countQuery = "SELECT count(p) FROM Patente p")
    Page<Patente> buscarTodasParaVitrine(Pageable pageable);

    @Modifying
    @Query("UPDATE Patente p SET p.status = :novoStatus WHERE p.id IN :ids AND p.status = 'DISPONIVEL'")
    int atualizarStatusEmMassa(@Param("ids") List<UUID> ids, @Param("novoStatus") StatusPatente novoStatus);
}