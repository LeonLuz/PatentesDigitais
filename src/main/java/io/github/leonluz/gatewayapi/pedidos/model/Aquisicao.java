package io.github.leonluz.gatewayapi.pedidos.model;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AQUISICAO")
public class Aquisicao {

    @Id
    @Column(name = "id_aquisicao")
    private String idAquisicao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario idUsuario;

    @Column(name = "data_aquisicao") //nullable = false)
    private LocalDate dataAquisicao;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status") //nullable = false)
    private StatusAquisicao statusAquisicao;

    @OneToMany(mappedBy = "idAquisicao", cascade = CascadeType.ALL)
    private List<ItemAquisicao> itens = new ArrayList<>();
    public Aquisicao() {

    }

    public void setIdAquisicao(String idAquisicao) {
        this.idAquisicao = idAquisicao;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public void setDataExpiracao(LocalDate dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public void setStatusAquisicao(StatusAquisicao statusAquisicao) {
        this.statusAquisicao = statusAquisicao;
    }

    public String getIdAquisicao() {
        return idAquisicao;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public LocalDate getDataExpiracao() {
        return dataExpiracao;
    }

    public StatusAquisicao getStatusAquisicao() {
        return statusAquisicao;
    }

    @Override
    public String toString() {
        return "Aquisicao{" +
                "idAquisicao='" + idAquisicao + '\'' +
                ", idUsuario=" + idUsuario +
                ", dataAquisicao=" + dataAquisicao +
                ", dataExpiracao=" + dataExpiracao +
                ", statusAquisicao=" + statusAquisicao +
                '}';
    }
}
