package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorUpdateDTO;
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

        pesquisador.setIdUsuario(UUID.randomUUID());
        pesquisador.setTipoPerfil(TipoPerfil.PESQUISADOR);

        try{
            Pesquisador pesquisadorSalvo = pesquisadorRepository.saveAndFlush(pesquisador);

            Carrinho novoCarrinho = new Carrinho(pesquisadorSalvo);
            carrinhoRepository.save(novoCarrinho);

            return pesquisadorSalvo;
        }
        catch (org.springframework.dao.DataIntegrityViolationException e){
            String mensagemBanco = e.getRootCause() != null ? e.getRootCause().getMessage().toLowerCase() : "";

            if(mensagemBanco.contains("cpf"))
                throw new IllegalArgumentException("CPF já cadastrado!");
            else if(mensagemBanco.contains("email"))
                throw new IllegalArgumentException("E-mail já cadastrado!");

            throw new  IllegalArgumentException(mensagemBanco);
        }
    }

    @Transactional
    public Pesquisador atualizarPesquisador(UUID id, PesquisadorUpdateDTO dto) {
        Pesquisador pesquisadorExistente = pesquisadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pesquisador não encontrado"));

        pesquisadorExistente.setEmail(dto.email());
        pesquisadorExistente.setTelefone(dto.telefone());
        pesquisadorExistente.setEndereco(dto.endereco());
        pesquisadorExistente.setNome(dto.nome());
        pesquisadorExistente.setDisponibilidadeConsultoria(dto.disponibilidadeConsultoria());

        return pesquisadorRepository.save(pesquisadorExistente);
    }

    public Pesquisador findByNome(String nome) {
        return pesquisadorRepository.findByNome(nome);
    }
}
