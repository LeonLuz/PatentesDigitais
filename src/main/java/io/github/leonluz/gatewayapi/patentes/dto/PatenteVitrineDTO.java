package io.github.leonluz.gatewayapi.patentes.dto;

import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.patentes.model.Patente;

import java.math.BigDecimal;
import java.util.UUID;

public class PatenteVitrineDTO {
    
    private UUID codigoPatente; 
    private String titulo;
    private String resumoCurto;
    private BigDecimal valor;
    private String nomeTitular; 

    public PatenteVitrineDTO(Patente patente) {
        this.codigoPatente = patente.getId(); 
        this.titulo = patente.getTitulo();
        this.valor = patente.getValor();
        
        if (patente.getResumo() != null && patente.getResumo().length() > 100) {
            this.resumoCurto = patente.getResumo().substring(0, 100) + "...";
        } else {
            this.resumoCurto = patente.getResumo();
        }

        if (patente.getIdTitular() != null) {
            if (patente.getIdTitular() instanceof Pesquisador) {
                this.nomeTitular = ((Pesquisador) patente.getIdTitular()).getNome();
                
            } else if (patente.getIdTitular() instanceof Organizacao) {
                this.nomeTitular = ((Organizacao) patente.getIdTitular()).getRazaoSocial();
                
            } else {
                this.nomeTitular = patente.getIdTitular().getEmail();
            }
        }
    }

    public UUID getCodigoPatente() { return codigoPatente; }
    public String getTitulo() { return titulo; }
    public String getResumoCurto() { return resumoCurto; }
    public BigDecimal getValor() { return valor; }
    public String getNomeTitular() { return nomeTitular; }
}