package io.github.leonluz.gatewayapi.autenticacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ORGANIZACAO")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("ORGANIZACAO")
public class Organizacao extends Usuario {

    @Column(name = "CNPJ") //unique = true)
    private String cnpj;

    @Column(name = "razao_social") //nullable = false)
    private String razaoSocial;

    public Organizacao() {

    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    @Override
    public String toString() {
        return "Organizacao{" +
                "cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                '}';
    }
}
