package io.github.leonluz.gatewayapi.patentes.controller;

import io.github.leonluz.gatewayapi.patentes.dto.PatenteRequestDTO;
import io.github.leonluz.gatewayapi.patentes.model.Patente;
import io.github.leonluz.gatewayapi.patentes.model.StatusPatente;
import io.github.leonluz.gatewayapi.patentes.service.PatenteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patentes")
public class PatenteController {

    private final PatenteService patenteService;

    public PatenteController(PatenteService patenteService) {
        this.patenteService = patenteService;
    }

    @GetMapping
    public ResponseEntity<List<Patente>> listarPatentes() {
        return ResponseEntity.ok(patenteService.listarTodas());
    }

    @PatchMapping("/{idPatente}/status")
    public ResponseEntity<String> alterarStatus(
            @PathVariable UUID idPatente,
            @RequestParam StatusPatente novoStatus,
            @RequestHeader("X-Usuario-Id") String idUsuarioResponsavel) {

        patenteService.atualizarStatus(idPatente, novoStatus, idUsuarioResponsavel);
        return ResponseEntity.ok("Status da patente atualizado com sucesso.");
    }

    @PostMapping("/{id}")
    public ResponseEntity<Patente> salvarPatente(@PathVariable("id") UUID idUsuario, @RequestBody PatenteRequestDTO dto) {
        Patente novaPatente = patenteService.salvarPatente(idUsuario, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPatente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patente> buscarPatente(@PathVariable("id") UUID id) {
        Patente patente = patenteService.buscarPatentePorId(id);
        return patente != null ? ResponseEntity.ok(patente) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patente> atualizarPatente(@PathVariable("id") UUID id, @RequestBody PatenteRequestDTO dto) {
        Patente patenteAtualizada = patenteService.atualizarPatente(id, dto);
        return ResponseEntity.ok(patenteAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPatente(@PathVariable("id") UUID id) {
        patenteService.deletarPatente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/baixar-pdf")
    public void baixarPdf(@PathVariable UUID id, HttpServletResponse response) throws IOException {
        byte[] pdfBytes = patenteService.obterPdfDaPatente(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"patente.pdf\"");

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/ids")
    public ResponseEntity<List<String>> listarIdPatentes(){
        return ResponseEntity.ok(patenteService.listarIdPatentes());
    }
}