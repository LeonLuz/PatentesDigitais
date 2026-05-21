package io.github.leonluz.gatewayapi.patentes.controller;

import io.github.leonluz.gatewayapi.patentes.dto.PatenteRequestDTO;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
<<<<<<< HEAD
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.service.PatenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
=======
import io.github.leonluz.gatewayapi.patentes.service.PatenteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
>>>>>>> main

@RestController
@RequestMapping("/api/patentes")
public class PatenteController {

<<<<<<< HEAD
    private final PatenteService patenteService;

    public PatenteController(PatenteService patenteService) {
        this.patenteService = patenteService;
=======
    private PatenteService patenteService;

    public PatenteController(PatenteService patenteService) {
        this.patenteService = patenteService;
    }

    @PostMapping("/{id}")
    public Patente salvarPatente(@PathVariable("id") String idUsuario, @RequestBody PatenteRequestDTO dto) {
        return  patenteService.salvarPatente(idUsuario, dto);
    }

    @GetMapping("/{id}")
    public Patente buscarPatente(@PathVariable("id") String id) {
        return  patenteService.buscarPatentePorId(id);
    }

    @PutMapping("/{id}")
    public Patente atualizarPatente(@PathVariable("id") String id, @RequestBody PatenteRequestDTO dto){
        return  patenteService.atualizarPatente(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletarPatente(@PathVariable("id") String id) {
        patenteService.deletarPatente(id);
    }

    @GetMapping("/{id}/baixar-pdf")
    public void baixarPdf(@PathVariable String id, HttpServletResponse response) throws IOException {

        byte[] pdfBytes = patenteService.obterPdfDaPatente(id);

        response.setContentType("application/pdf");

        response.setHeader("Content-Disposition", "inline; filename=\"patente.pdf\"");

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
>>>>>>> main
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