package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.dto.AquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.service.AquisicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/aquisicoes")
public class AquisicaoController {

    private final AquisicaoService aquisicaoService;

    public AquisicaoController(AquisicaoService aquisicaoService) {
        this.aquisicaoService = aquisicaoService;
    }

    @PostMapping("/checkout/{idUsuario}")
    public ResponseEntity<String> realizarCheckout(@PathVariable UUID idUsuario) {
        try {
            UUID idAquisicao = aquisicaoService.finalizarCheckout(idUsuario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Checkout finalizado com sucesso! ID da Transação: " + idAquisicao);
        } catch (IllegalStateException e) {
            // Retornar 409 para concorrência
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor: " + e.getMessage());
        }
    }

    @PutMapping("/cancelar/{idAquisicao}")
    public ResponseEntity<String> cancelarAquisicao(@PathVariable UUID idAquisicao) {
        aquisicaoService.cancelarAquisicao(idAquisicao);
        return ResponseEntity.ok("Aquisição cancelada e patentes liberadas!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aquisicao> buscarPorId(@PathVariable UUID id) {
        Aquisicao aquisicao = aquisicaoService.buscarPorId(id);
        return ResponseEntity.ok(aquisicao);
    }
}