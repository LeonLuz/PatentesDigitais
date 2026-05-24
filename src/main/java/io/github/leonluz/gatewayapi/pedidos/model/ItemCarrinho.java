package io.github.leonluz.gatewayapi.pedidos.model;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "ITEM_CARRINHO")
public class ItemCarrinho {

    @Id
    @Column(name = "id_item")
    private UUID idItem;

    @ManyToOne
    @JoinColumn(name = "id_carrinho", nullable = false)
    private Carrinho idCarrinho;

    @ManyToOne
    @JoinColumn(name = "id_patente", nullable = false)
    private Patente idPatente;



    public ItemCarrinho() {
    }

    public void setIdItem(UUID idItem) {
        this.idItem = idItem;
    }

    public void setIdCarrinho(Carrinho idCarrinho) {
        this.idCarrinho = idCarrinho;
    }

    public void setIdPatente(Patente idPatente) {
        this.idPatente = idPatente;
    }

    public UUID getIdItem() {
        return idItem;
    }

    public Carrinho getIdCarrinho() {
        return idCarrinho;
    }

    public Patente getIdPatente() {
        return idPatente;
    }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "idItem='" + idItem + '\'' +
                ", idCarrinho=" + idCarrinho +
                ", idPatente=" + idPatente +
                '}';
    }
}
