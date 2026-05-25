package io.github.leonluz.gatewayapi.pedidos.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCarrinho")
    @JsonIdentityReference(alwaysAsId = true)
    private Carrinho idCarrinho;

    @ManyToOne
    @JoinColumn(name = "id_patente", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Patente idPatente;

    public ItemCarrinho(Carrinho carrinho, Patente patente) {
        this.idItem = UUID.randomUUID(); // <-- Evita o erro de "Não foi possível salvar" no banco
        this.idCarrinho = carrinho;
        this.idPatente = patente;
    }

    public ItemCarrinho() {
    }

    public void setIdItem(UUID idItem) { this.idItem = idItem; }
    public void setIdCarrinho(Carrinho idCarrinho) { this.idCarrinho = idCarrinho; }
    public void setIdPatente(Patente idPatente) { this.idPatente = idPatente; }
    public UUID getIdItem() { return idItem; }
    public Carrinho getIdCarrinho() { return idCarrinho; }
    public Patente getIdPatente() { return idPatente; }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "idItem='" + idItem + '\'' +
                ", idCarrinho=" + idCarrinho +
                ", idPatente=" + idPatente +
                '}';
    }
}