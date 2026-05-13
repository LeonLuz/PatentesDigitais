package io.github.leonluz.gatewayapi.autenticacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "NIT")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("NIT")
public class NIT extends Usuario {

    @Column(name = "CNPJ") //unique = true)
    private String cnpj;

    @Column(name = "razao_social") //nullable = false)
    private String razaoSocial;

    public NIT() {

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
        return "NIT{" +
                "cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                '}';
    }
}