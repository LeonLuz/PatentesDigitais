package io.github.leonluz.gatewayapi.pedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "CARRINHO")
public class Carrinho {

    @Id
    @Column(name = "id_carrinho")
    private UUID idCarrinho;

    @OneToOne
    @JoinColumn(name = "id_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "idCarrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("idCarrinho")
    private List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho(Usuario usuario) {
        this.idCarrinho = java.util.UUID.randomUUID();
        this.usuario = usuario;
    }

    public Carrinho() {

    }

    public void setIdCarrinho(UUID idCarrinho) {
        this.idCarrinho = idCarrinho;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public UUID getIdCarrinho() {
        return idCarrinho;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    @Override
    public String toString() {
        return "Carrinho{" +
                "idCarrinho='" + idCarrinho + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}
