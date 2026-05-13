package io.github.leonluz.gatewayapi.pedidos.model;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import jakarta.persistence.*;

@Entity
@Table(name = "ITEM_AQUISICAO")
public class ItemAquisicao {

    @Id
    @Column(name = "id_item")
    private String idItem;

    @ManyToOne
    @JoinColumn(name = "id_aquisicao")
    private Aquisicao idAquisicao;

    @ManyToOne
    @JoinColumn(name = "id_patente")
    private Patente patente;

    public ItemAquisicao() {

    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public void setIdAquisicao(Aquisicao idAquisicao) {
        this.idAquisicao = idAquisicao;
    }

    public void setPatente(Patente patente) {
        this.patente = patente;
    }

    public String getIdItem() {
        return idItem;
    }

    public Aquisicao getIdAquisicao() {
        return idAquisicao;
    }

    public Patente getPatente() {
        return patente;
    }

    @Override
    public String toString() {
        return "ItemAquisicao{" +
                "idItem='" + idItem + '\'' +
                ", idAquisicao=" + idAquisicao +
                ", patente=" + patente +
                '}';
    }
}
