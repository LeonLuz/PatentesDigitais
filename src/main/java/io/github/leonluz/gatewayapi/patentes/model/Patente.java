package io.github.leonluz.gatewayapi.patentes.model;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PATENTE")

public class Patente {
    @Id
    @Column(name = "id_patente")
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_titular")
    private Usuario titular;

    @Column(name = "titulo")
    private  String titulo;

    @Column(name = "num_deposito")
    private String numDeposito;

    @Column(name = "resumo")
    private String resumo;

    @Column(name = "area")
    private String area;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "pesquisadores")
    private String pesquisadores;

    @Enumerated(EnumType.STRING)
    @Column(name = "status") //nullable = false)
    private StatusPatente status;

    @ManyToMany
    @JoinTable(
            name = "PATENTE_PESQUISADOR",
            joinColumns = @JoinColumn(name = "id_patente"),
            inverseJoinColumns = @JoinColumn(name = "id_pesquisador")
    )
    private List<Pesquisador> pesquisadoresAssociados = new ArrayList<>();

    public Patente() {
        //lembrar de usar os setters e gerar o id depois
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitular(Usuario titular) {
        this.titular = titular;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setNumDeposito(String numDeposito) {
        this.numDeposito = numDeposito;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public void setPesquisadores(String pesquisadores) {
        this.pesquisadores = pesquisadores;
    }

    public void setStatus(StatusPatente status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Usuario getTitular() {
        return titular;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getNumDeposito() {
        return numDeposito;
    }

    public String getResumo() {
        return resumo;
    }

    public String getArea() {
        return area;
    }

    public Double getValor() {
        return valor;
    }

    public String getPesquisadores() {
        return pesquisadores;
    }

    public StatusPatente getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Patente{" +
                "id='" + id + '\'' +
                ", titular='" + titular + '\'' +
                ", titulo='" + titulo + '\'' +
                ", numDeposito='" + numDeposito + '\'' +
                ", resumo='" + resumo + '\'' +
                ", area='" + area + '\'' +
                ", valor=" + valor +
                ", pesquisadores='" + pesquisadores + '\'' +
                ", status=" + status +
                '}';
    }
}
