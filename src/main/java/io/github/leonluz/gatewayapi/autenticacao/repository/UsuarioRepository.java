package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    @Modifying
    @Query(value = "INSERT INTO USUARIO_ASSOCIADO (id_instituicao, id_usuario_associado) VALUES (:idInstituicao, :idAssociado)", nativeQuery = true)
    void adicionarVinculo(@Param("idInstituicao") String idInstituicao, @Param("idAssociado") String idAssociado);

    @Modifying
    @Query(value = "DELETE FROM USUARIO_ASSOCIADO WHERE id_instituicao = :idInstituicao AND id_usuario_associado = :idAssociado", nativeQuery = true)
    void removerVinculo(@Param("idInstituicao") String idInstituicao, @Param("idAssociado") String idAssociado);

    @Query(value = "SELECT COUNT(*) FROM USUARIO_ASSOCIADO WHERE id_instituicao = :idInstituicao AND id_usuario_associado = :idAssociado", nativeQuery = true)
    int existsVinculo(@Param("idInstituicao") String idInstituicao, @Param("idAssociado") String idAssociado);
}