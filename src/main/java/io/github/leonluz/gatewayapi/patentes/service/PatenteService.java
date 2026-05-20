package io.github.leonluz.gatewayapi.patentes.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.model.TipoPerfil;
import io.github.leonluz.gatewayapi.autenticacao.repository.PesquisadorRepository;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import io.github.leonluz.gatewayapi.patentes.dto.PatenteRequestDTO;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
public class PatenteService {

    private final PatenteRepository patenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PesquisadorRepository pesquisadorRepository;

    public PatenteService(PatenteRepository patenteRepository,
                          UsuarioRepository usuarioRepository,
                          PesquisadorRepository pesquisadorRepository) {
        this.patenteRepository = patenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pesquisadorRepository = pesquisadorRepository;
    }

    public Patente salvarPatente(String idUsuario, PatenteRequestDTO dto) {

        Patente patente = new Patente(dto);
        patente.setId(UUID.randomUUID().toString());

        Usuario titular = usuarioRepository.findById(dto.idTitular())
                .orElseThrow(() -> new RuntimeException("Titular não encontrado"));
        patente.setIdTitular(titular);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));;

        if (usuario.getTipoPerfil() == TipoPerfil.NIT) {
            patente.setStatus(StatusPatente.DISPONIVEL);
        }
        else {
            patente.setStatus(StatusPatente.RASCUNHO);
        }


        // tabela PATENTE_PESQUISADOR
        if (dto.idsPesquisadoresAssociados() != null && !dto.idsPesquisadoresAssociados().isEmpty()) {
            List<Pesquisador> pesquisadores = dto.idsPesquisadoresAssociados().stream()
                    .map(pesquisadorRepository::getReferenceById)
                    .toList();
            patente.getPesquisadoresAssociados().addAll(pesquisadores);
        }

        return patenteRepository.save(patente);
    }

    public Patente buscarPatentePorId(String id) {
        return patenteRepository.findById(id).orElse(null);
    }

    public Patente atualizarPatente(String id, PatenteRequestDTO dto) {
        Patente patenteExistente = patenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

        patenteExistente.setTitulo(dto.titulo());
        patenteExistente.setNumDeposito(dto.numDeposito());
        patenteExistente.setResumo(dto.resumo());
        patenteExistente.setArea(dto.area());
        patenteExistente.setValor(dto.valor());
        patenteExistente.setPesquisadores(dto.pesquisadores());

        Usuario titular = usuarioRepository.getReferenceById(dto.idTitular());
        patenteExistente.setIdTitular(titular);

        return patenteRepository.save(patenteExistente);
    }

    public void deletarPatente(String id) {
        patenteRepository.deleteById(id);
    }

    public byte[] obterPdfDaPatente(String idPatente) {

        Patente patente = patenteRepository.findById(idPatente)
                .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

        String caminhoRelativo = patente.getDocumento();

        File arquivo = new File(caminhoRelativo);

        if (!arquivo.exists()) {
            throw new RuntimeException("O arquivo PDF não foi encontrado na pasta do projeto!");
        }

        try {
            return Files.readAllBytes(arquivo.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler o arquivo PDF", e);
        }
    }
}