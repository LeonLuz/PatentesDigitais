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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@Service
public class PatenteService {

    // Logger estruturado para registro e monitoramento de eventos de segurança/auditoria
    private static final Logger logger = LoggerFactory.getLogger(PatenteService.class);

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

    @Transactional(readOnly = true)
    public List<Patente> listarTodas() {
        return patenteRepository.findAll();
    }

    @Transactional
    public void atualizarStatus(UUID idPatente, StatusPatente novoStatus, String idUsuarioResponsavel) {
        Patente patente = patenteRepository.findById(idPatente)
                .orElseThrow(() -> new IllegalArgumentException("Patente não localizada no sistema."));

        StatusPatente statusAntigo = patente.getStatus();

        // Aqui, futuramente, entrará a validação de autorização:
        // Este idUsuarioResponsavel é o titular da patente ou um admin?

        patente.setStatus(novoStatus);
        patenteRepository.save(patente);

        // Registro do evento para garantir rastreabilidade das transações sensíveis
        logger.info("AUDIT - STATUS ALTERADO: Patente [{}] mudou de [{}] para [{}] sob o comando do usuário [{}]",
                idPatente, statusAntigo, novoStatus, idUsuarioResponsavel);
    }

    @Transactional
    public Patente salvarPatente(UUID idTitular, PatenteRequestDTO dto) {
        Usuario titular = usuarioRepository.findById(idTitular)
                .orElseThrow(() -> new RuntimeException("Titular não encontrado"));

        Patente patente = new Patente();

        // Use os nomes dos métodos que você criou na classe Patente:
        patente.setId(UUID.randomUUID());        // Era setIdPatente
        patente.setIdTitular(titular);           // Era setTitular

        patente.setTitulo(dto.titulo());
        patente.setNumDeposito(dto.numDeposito());
        patente.setResumo(dto.resumo());
        patente.setArea(dto.area());
        patente.setValor(dto.valor());
        patente.setPesquisadores(dto.pesquisadores());
        patente.setDocumento(dto.documento());

        // Certifique-se que o status no DTO existe e não é nulo
        if (dto.status() != null) {
            patente.setStatus(StatusPatente.valueOf(dto.status().toUpperCase()));
        }

        // Associar pesquisadores
        if (dto.idsPesquisadoresAssociados() != null && !dto.idsPesquisadoresAssociados().isEmpty()) {
            List<Pesquisador> pesquisadores = dto.idsPesquisadoresAssociados().stream()
                    .map(pesquisadorRepository::getReferenceById)
                    .toList();
            patente.getPesquisadoresAssociados().addAll(pesquisadores);
        }

        return patenteRepository.save(patente);
    }

    @Transactional(readOnly = true)
    public Patente buscarPatentePorId(UUID id) {
        return patenteRepository.findById(id).orElse(null);
    }

    @Transactional
    public Patente atualizarPatente(UUID id, PatenteRequestDTO dto) {
        Patente patenteExistente = patenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

        patenteExistente.setTitulo(dto.titulo());
        patenteExistente.setNumDeposito(dto.numDeposito());
        patenteExistente.setResumo(dto.resumo());
        patenteExistente.setArea(dto.area());
        patenteExistente.setValor(dto.valor());
        patenteExistente.setPesquisadores(dto.pesquisadores());

        // Se você precisa atualizar o titular na edição, use o ID da URL ou busque o objeto corretamente
        // Nota: Removido dto.idTitular() pois o DTO não tem mais esse campo

        return patenteRepository.save(patenteExistente);
    }

    @Transactional
    public void deletarPatente(UUID id) {
        patenteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public byte[] obterPdfDaPatente(UUID idPatente) {
        Patente patente = patenteRepository.findById(idPatente)
                .orElseThrow(() -> new RuntimeException("Patente não encontrada"));

        String caminhoRelativo = patente.getDocumento();
        if (caminhoRelativo == null) {
            throw new RuntimeException("Caminho do documento não especificado para esta patente.");
        }

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

    @Transactional(readOnly = true)
    public List<String> listarIdPatentes() {
        return this.patenteRepository.listarTodosIds();
    }
}
