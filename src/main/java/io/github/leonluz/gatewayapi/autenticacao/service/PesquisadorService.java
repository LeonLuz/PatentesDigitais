package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.PesquisadorRepository;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.repository.CarrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PesquisadorService {
    private final PesquisadorRepository pesquisadorRepository;
    private final CarrinhoRepository carrinhoRepository;

    public PesquisadorService(PesquisadorRepository pesquisadorRepository, CarrinhoRepository carrinhoRepository) {
        this.pesquisadorRepository = pesquisadorRepository;
        this.carrinhoRepository = carrinhoRepository;
    }

    @Transactional
    public Pesquisador salvarPesquisador(PesquisadorRequestDTO dto) {

        Pesquisador pesquisador = new Pesquisador(dto);

        pesquisador.setIdUsuario(UUID.randomUUID().toString());
        pesquisador.setTipoPerfil(TipoPerfil.PESQUISADOR);

        Pesquisador pesquisadorSalvo = pesquisadorRepository.save(pesquisador);

        Carrinho novoCarrinho = new Carrinho(pesquisadorSalvo);
        carrinhoRepository.save(novoCarrinho);

        return pesquisadorSalvo;
    }

    @Transactional
    public Pesquisador atualizarPesquisador(String id, PesquisadorRequestDTO dto) {
        Pesquisador pesquisadorExistente = pesquisadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesquisador não encontrado"));

        pesquisadorExistente.setEmail(dto.email());
        pesquisadorExistente.setSenha(dto.senha());
        pesquisadorExistente.setTelefone(dto.telefone());
        pesquisadorExistente.setEndereco(dto.endereco());
        pesquisadorExistente.setNome(dto.nome());
        pesquisadorExistente.setCpf(dto.cpf());
        pesquisadorExistente.setStatusAtivo(true);
        pesquisadorExistente.setStatusAuth(false);

        return pesquisadorRepository.save(pesquisadorExistente);
    }

    public Pesquisador findByNome(String nome) {
        return pesquisadorRepository.findByNome(nome);
    }
}