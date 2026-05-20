package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.NITRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NITService {
    private final NITRepository  nitRepository;

    public NITService(NITRepository nitRepository) {
        this.nitRepository = nitRepository;
    }

    public NIT salvarNit(NITRequestDTO dto) {
        NIT nit = new NIT(dto);

        nit.setIdUsuario(UUID.randomUUID().toString());
        nit.setTipoPerfil(TipoPerfil.NIT);
        nit.setStatusAtivo(true);
        nit.setStatusAuth(false);

        return nitRepository.save(nit);
    }

    public NIT atualizarNit(String id, NITRequestDTO dto) {
        NIT nitExistente = nitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NIT não encontrado"));

            nitExistente.setEmail(dto.email());
            nitExistente.setSenha(dto.senha());
            nitExistente.setTelefone(dto.telefone());
            nitExistente.setEndereco(dto.endereco());
            nitExistente.setRazaoSocial(dto.razaoSocial());

        return nitRepository.save(nitExistente);
    }

    public NIT findByRazaoSocial(String razaoSocial) {
        return nitRepository.findByRazaoSocial(razaoSocial);
    }
}
