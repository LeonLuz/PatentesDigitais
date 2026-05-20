package io.github.leonluz.gatewayapi.autenticacao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NIT")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("NIT")
public class NIT extends Usuario {

    @Column(name = "CNPJ", unique = true)
    private String cnpj;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @OneToMany(mappedBy = "nit")
    @JsonIgnoreProperties("nit")
    private List<Pesquisador> pesquisadores = new ArrayList<>();

    public NIT(NITRequestDTO dto) {
        this.setEmail(dto.email());
        this.setSenha(dto.senha());
        this.setTelefone(dto.telefone());
        this.setEndereco(dto.endereco());
        this.setCnpj(dto.cnpj());
        this.setRazaoSocial(dto.razaoSocial());
    }

    public NIT() {}

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public void setPesquisadores(List<Pesquisador> pesquisadores) {
        this.pesquisadores = pesquisadores;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public List<Pesquisador> getPesquisadores() {
        return pesquisadores;
    }

    @Override
    public String toString() {
        return "NIT{" +
                "cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                '}';
    }
}