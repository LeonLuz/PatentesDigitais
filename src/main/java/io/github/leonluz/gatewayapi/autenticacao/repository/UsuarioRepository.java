package io.github.leonluz.gatewayapi.autenticacao.repository;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    @Modifying
    @Query(value = "INSERT INTO USUARIO_ASSOCIADO (id_instituicao, id_usuario_associado) " + "VALUES (UUID_TO_BIN(:idInstituicao), UUID_TO_BIN(:idAssociado))", nativeQuery = true)
    void adicionarVinculo(@Param("idInstituicao") UUID idInstituicao, @Param("idAssociado") UUID idAssociado);

    @Modifying
    @Query(value = "DELETE FROM USUARIO_ASSOCIADO " + "WHERE id_instituicao = UUID_TO_BIN(:idInstituicao) " + "AND id_usuario_associado = UUID_TO_BIN(:idAssociado)", nativeQuery = true)
    void removerVinculo(@Param("idInstituicao") UUID idInstituicao, @Param("idAssociado") UUID idAssociado);

    @Query(value = "SELECT COUNT(*) FROM USUARIO_ASSOCIADO " + "WHERE id_instituicao = UUID_TO_BIN(:idInstituicao) " + "AND id_usuario_associado = UUID_TO_BIN(:idAssociado)", nativeQuery = true)
    int existsVinculo(@Param("idInstituicao") UUID idInstituicao, @Param("idAssociado") UUID idAssociado);
}