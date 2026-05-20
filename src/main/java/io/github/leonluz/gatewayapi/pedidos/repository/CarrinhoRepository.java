package io.github.leonluz.gatewayapi.pedidos.repository;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarrinhoRepository extends JpaRepository<Patente, String> {
    @Query(value = "SELECT id_carrinho FROM CARRINHO WHERE id_usuario = :idUsuario", nativeQuery = true)
    String buscarIdCarrinhoPorUsuario(@Param("idUsuario") String idUsuario);

    @Modifying
    @Query(value = "INSERT INTO CARRINHO (id_carrinho, id_usuario) VALUES (:idCarrinho, :idUsuario)", nativeQuery = true)
    void criarCarrinho(@Param("idCarrinho") String idCarrinho, @Param("idUsuario") String idUsuario);

    @Modifying
    @Query(value = "INSERT INTO ITEM_CARRINHO (id_item, id_carrinho, id_patente) VALUES (:idItem, :idCarrinho, :idPatente)", nativeQuery = true)
    void adicionarItem(@Param("idItem") String idItem, @Param("idCarrinho") String idCarrinho, @Param("idPatente") String idPatente);

    @Modifying
    @Query(value = "DELETE FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho AND id_patente = :idPatente", nativeQuery = true)
    void removerItem(@Param("idCarrinho") String idCarrinho, @Param("idPatente") String idPatente);

    @Query(value = "SELECT COUNT(*) FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho AND id_patente = :idPatente", nativeQuery = true)
    int verificarItemExistente(@Param("idCarrinho") String idCarrinho, @Param("idPatente") String idPatente);

    @Query(value = "SELECT id_patente FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho", nativeQuery = true)
    List<String> listarItensDoCarrinho(@Param("idCarrinho") String idCarrinho);

    @Modifying
    @Query(value = "DELETE FROM ITEM_CARRINHO WHERE id_carrinho = :idCarrinho", nativeQuery = true)
    void esvaziarCarrinho(@Param("idCarrinho") String idCarrinho);
}