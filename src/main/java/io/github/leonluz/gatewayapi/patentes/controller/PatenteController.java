package io.github.leonluz.gatewayapi.patentes.controller;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.service.PatenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patentes")
public class PatenteController {

    private final PatenteService patenteService;

    public PatenteController(PatenteService patenteService) {
        this.patenteService = patenteService;
    }

    // 1. Endpoint para leitura (Vitrine)
    @GetMapping
    public ResponseEntity<List<Patente>> listarPatentes() {
        return ResponseEntity.ok(patenteService.listarTodas());
    }

    // 2. Endpoint para atualização de status (Apenas campos específicos)
    @PatchMapping("/{idPatente}/status")
    public ResponseEntity<String> alterarStatus(
            @PathVariable String idPatente,
            @RequestParam StatusPatente novoStatus,
            @RequestHeader("X-Usuario-Id") String idUsuarioResponsavel) { 
            // Simulando a captura do usuário logado via cabeçalho HTTP por enquanto
            
        patenteService.atualizarStatus(idPatente, novoStatus, idUsuarioResponsavel);
        return ResponseEntity.ok("Status da patente atualizado com sucesso.");
    }
}