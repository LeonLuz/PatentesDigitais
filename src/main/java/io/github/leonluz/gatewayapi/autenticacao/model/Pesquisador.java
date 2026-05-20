package io.github.leonluz.gatewayapi.autenticacao.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorRequestDTO;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "PESQUISADOR")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("PESQUISADOR")
public class Pesquisador extends Usuario {

    @Column(name = "CPF", unique = true)
    private String cpf;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "disponibilidade_consultoria")
    private boolean disponibilidadeConsultoria;

    @ManyToMany(mappedBy = "pesquisadoresAssociados")
    private List<Patente> patentesAssociadas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_nit")
    @JsonIgnoreProperties("pesquisadores")
    private Usuario nit;

    public Pesquisador(PesquisadorRequestDTO dto) {
        this.setEmail(dto.email());
        this.setSenha(dto.senha());
        this.setTelefone(dto.telefone());
        this.setEndereco(dto.endereco());
        this.cpf = dto.cpf();
        this.nome = dto.nome();
        this.disponibilidadeConsultoria = dto.disponibilidadeConsultoria();
    }

    public Pesquisador() {

    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDisponibilidadeConsultoria(boolean disponibilidadeConsultoria) {
        this.disponibilidadeConsultoria = disponibilidadeConsultoria;
    }

    public void setPatentesAssociadas(List<Patente> patentesAssociadas) {
        this.patentesAssociadas = patentesAssociadas;
    }

    public void setNit(Usuario nit) {
        this.nit = nit;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public boolean isDisponibilidadeConsultoria() {
        return disponibilidadeConsultoria;
    }

    public List<Patente> getPatentesAssociadas() {
        return patentesAssociadas;
    }

    public Usuario getNit() {
        return nit;
    }

    @Override
    public String toString() {
        return "Pesquisador{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", disponibilidadeConsultoria=" + disponibilidadeConsultoria +
                ", patentesAssociadas=" + patentesAssociadas +
                ", nit=" + nit +
                '}';
    }
}
