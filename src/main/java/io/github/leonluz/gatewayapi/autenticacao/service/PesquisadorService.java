package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.PesquisadorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PesquisadorService {
    private PesquisadorRepository pesquisadorRepository;

    public PesquisadorService(PesquisadorRepository pesquisadorRepository) {
        this.pesquisadorRepository = pesquisadorRepository;
    }

    public Pesquisador salvarPesquisador(Pesquisador pesquisador) {
        System.out.println("Usuário pesquisador recebido: " + pesquisador);

        var id = UUID.randomUUID().toString();
        pesquisador.setIdUsuario(id);
        pesquisador.setTipoPerfil(TipoPerfil.PESQUISADOR);

        pesquisadorRepository.save(pesquisador);
        return pesquisador;
    }

    public Pesquisador atualizarPesquisador(String id, Pesquisador pesquisador) {
        pesquisador.setIdUsuario(id);
        return  pesquisadorRepository.save(pesquisador);
    }

    public Pesquisador findByNome(String nome) {
        return pesquisadorRepository.findByNome(nome);
    }
}
