package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AquisicaoRepository extends JpaRepository<Aquisicao, UUID> {

  @Modifying
  @Query(value = "INSERT INTO aquisicao (id_aquisicao, id_usuario, data_aquisicao, status) VALUES (:idAquisicao, :idUsuario, NOW(), 'AGUARDANDO_PAGAMENTO')", nativeQuery = true)
  void criarAquisicao(@Param("idAquisicao") UUID idAquisicao, @Param("idUsuario") UUID idUsuario);

  @Modifying
  @Query(value = "INSERT INTO item_aquisicao (id_item, id_aquisicao, id_patente) VALUES (:idItem, :idAquisicao, :idPatente)", nativeQuery = true)
  void adicionarItemAquisicao(@Param("idItem") UUID idItem, @Param("idAquisicao") UUID idAquisicao,
      @Param("idPatente") UUID idPatente);
}

