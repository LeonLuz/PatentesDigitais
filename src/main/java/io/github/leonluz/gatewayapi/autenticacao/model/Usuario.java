package io.github.leonluz.gatewayapi.autenticacao.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="USUARIO")
@Inheritance(strategy= InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_perfil")
public abstract class Usuario {

    @Id
    @Column(name = "id_usuario", columnDefinition = "BINARY(16)")
    private UUID idUsuario;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "endereco")
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_perfil", insertable = false, updatable = false)
    private TipoPerfil tipoPerfil;

    @Column(name = "status_auth")
    private boolean statusAuth;

    @Column(name = "status_ativo")
    private boolean statusAtivo;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private OffsetDateTime dataCriacao;

    public Usuario() {

    }

    public void setIdUsuario(UUID idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public void setStatusAuth(boolean statusAuth) {
        this.statusAuth = statusAuth;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public void setDataCriacao(OffsetDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UUID getIdUsuario() {
        return idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public boolean isStatusAuth() {
        return statusAuth;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public OffsetDateTime getDataCriacao() {
        return dataCriacao;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco='" + endereco + '\'' +
                ", tipoPerfil=" + tipoPerfil +
                ", statusAuth=" + statusAuth +
                ", statusAtivo=" + statusAtivo +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
