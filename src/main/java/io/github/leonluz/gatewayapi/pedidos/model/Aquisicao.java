package io.github.leonluz.gatewayapi.pedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @JsonIgnoreProperties("idAquisicao")
    private List<ItemAquisicao> itens = new ArrayList<>();

    public Aquisicao(Usuario usuarioComprador) {
        this.idAquisicao = UUID.randomUUID().toString();
        this.idUsuario = usuarioComprador;
        this.dataAquisicao = LocalDate.now();
        this.dataExpiracao = LocalDate.now().plusDays(3);
        this.statusAquisicao = StatusAquisicao.AGUARDANDO_PAGAMENTO;
    }

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

    public void setItens(List<ItemAquisicao> itens) {
        this.itens = itens;
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

    public List<ItemAquisicao> getItens() {
        return itens;
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
