package io.github.leonluz.gatewayapi.patentes.service;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.repository.PatenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatenteService {

    // Logger estruturado para registro e monitoramento de eventos de segurança/auditoria
    private static final Logger logger = LoggerFactory.getLogger(PatenteService.class);
    
    private final PatenteRepository patenteRepository;

    public PatenteService(PatenteRepository patenteRepository) {
        this.patenteRepository = patenteRepository;
    }

    public List<Patente> listarTodas() {
        return patenteRepository.findAll();
    }

    @Transactional
    public void atualizarStatus(String idPatente, StatusPatente novoStatus, String idUsuarioResponsavel) {
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
}