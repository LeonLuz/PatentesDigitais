package io.github.leonluz.gatewayapi.patentes.model;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.patentes.dto.PatenteRequestDTO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PATENTE")

public class Patente {
    @Id
    @Column(name = "id_patente")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_titular")
    private Usuario idTitular;

    @Column(name = "titulo")
    private  String titulo;

    @Column(name = "num_deposito", unique = true)
    private String numDeposito;

    @Column(name = "resumo")
    private String resumo;

    @Column(name = "area")
    private String area;

    @Column(name = "valor")
    private Double valor;

    @Column(name = "pesquisadores")
    private String pesquisadores;

    @Column(name = "documento")
    private String documento;

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

    public Patente(PatenteRequestDTO dto) {
        this.id = UUID.randomUUID();
        this.titulo = dto.titulo();
        this.numDeposito = dto.numDeposito();
        this.resumo = dto.resumo();
        this.area = dto.area();
        this.valor = dto.valor();
        this.pesquisadores = dto.pesquisadores();
        this.documento = dto.documento();
    }

    public Patente() {

    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setIdTitular(Usuario idTitular) {
        this.idTitular = idTitular;
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

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setStatus(StatusPatente status) {
        this.status = status;
    }

    public void setPesquisadoresAssociados(List<Pesquisador> pesquisadoresAssociados) {
        this.pesquisadoresAssociados = pesquisadoresAssociados;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getIdTitular() {
        return idTitular;
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

    public String getDocumento() {
        return documento;
    }

    public StatusPatente getStatus() {
        return status;
    }

    public List<Pesquisador> getPesquisadoresAssociados() {
        return pesquisadoresAssociados;
    }

    @Override
    public String toString() {
        return "Patente{" +
                "id='" + id + '\'' +
                ", idTitular='" + idTitular.getIdUsuario().toString() + '\'' +
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
