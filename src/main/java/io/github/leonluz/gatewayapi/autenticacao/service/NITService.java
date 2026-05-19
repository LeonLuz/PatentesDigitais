package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.NITRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NITService {
    private NITRepository  nitRepository;

    public NITService(NITRepository nitRepository) {
        this.nitRepository = nitRepository;
    }

    public NIT salvarNit(NIT nit) {
        System.out.println("Usuário nit recebido: " + nit);

        var id = UUID.randomUUID().toString();
        nit.setIdUsuario(id);
        nit.setTipoPerfil(TipoPerfil.NIT);

        return nitRepository.save(nit);
    }

    public NIT atualizarNit(String id, NIT nit) {
        nit.setIdUsuario(id);
        return nitRepository.save(nit);
    }

    public NIT findByRazaoSocial(String razaoSocial) {
        return nitRepository.findByRazaoSocial(razaoSocial);
    }
}
