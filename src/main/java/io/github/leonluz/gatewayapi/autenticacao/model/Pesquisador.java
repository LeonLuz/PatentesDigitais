package io.github.leonluz.gatewayapi.autenticacao.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PESQUISADOR")
@PrimaryKeyJoinColumn(name = "id_usuario")
@DiscriminatorValue("PESQUISADOR")
public class Pesquisador extends Usuario {

    @Column(name = "CPF") //unique = true)
    private String cpf;

    @Column(name = "nome") //nullable = false)
    private String nome;

    @Column(name = "disponibilidade_consultoria")
    private boolean disponibilidadeConsultoria;

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

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public boolean isDisponibilidadeConsultoria() {
        return disponibilidadeConsultoria;
    }

    @Override
    public String toString() {
        return "Pesquisador{" +
                "cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", disponibilidadeConsultoria=" + disponibilidadeConsultoria +
                '}';
    }
}
