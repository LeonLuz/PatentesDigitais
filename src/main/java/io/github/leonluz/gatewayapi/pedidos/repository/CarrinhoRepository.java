package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, UUID> {

    @Query(value = "SELECT id_carrinho FROM CARRINHO WHERE id_usuario = :idUsuario", nativeQuery = true)
    UUID buscarIdCarrinhoPorUsuario(@Param("idUsuario") UUID idUsuario);

    @Modifying
    @Query(value = "INSERT INTO CARRINHO (id_carrinho, id_usuario) VALUES (:idCarrinho, :idUsuario)", nativeQuery = true)
    void criarCarrinho(@Param("idCarrinho") UUID idCarrinho, @Param("idUsuario") UUID idUsuario);

    @Modifying
    @Query(value = "INSERT INTO ITEM_CARRINHO (id_item, id_carrinho, id_patente) VALUES (:idItem, :idCarrinho, :idPatente)", nativeQuery = true)
    void adicionarItem(@Param("idItem") UUID idItem, @Param("idCarrinho") UUID idCarrinho, @Param("idPatente") UUID idPatente);

    @Modifying
    @Query(value = "DELETE FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho AND id_patente = :idPatente", nativeQuery = true)
    void removerItem(@Param("idCarrinho") UUID idCarrinho, @Param("idPatente") UUID idPatente);

    @Query(value = "SELECT COUNT(*) FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho AND id_patente = :idPatente", nativeQuery = true)
    int verificarItemExistente(@Param("idCarrinho") UUID idCarrinho, @Param("idPatente") UUID idPatente);

    @Query(value = "SELECT id_patente FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho", nativeQuery = true)
    List<UUID> listarItensDoCarrinho(@Param("idCarrinho") UUID idCarrinho);

    @Modifying
    @Query(value = "DELETE FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho", nativeQuery = true)
    void esvaziarCarrinho(@Param("idCarrinho") UUID idCarrinho);

    Optional<Carrinho> findByUsuario(Usuario usuario);
}
