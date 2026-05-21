package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.dto.AquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.service.AquisicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aquisicoes")
public class AquisicaoController {

    private final AquisicaoService aquisicaoService;

    public AquisicaoController(AquisicaoService aquisicaoService) {
        this.aquisicaoService = aquisicaoService;
    }

    @PostMapping("/checkout/{idUsuario}")
    public ResponseEntity<String> realizarCheckout(@PathVariable String idUsuario) {
        String idAquisicao = aquisicaoService.finalizarCheckout(idUsuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Checkout finalizado com sucesso! ID da Transação: " + idAquisicao);
    }

    @PostMapping
    public ResponseEntity<Aquisicao> criarAquisicao(@RequestBody AquisicaoRequestDTO dto) {
        Aquisicao novaAquisicao = aquisicaoService.criarAquisicao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAquisicao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aquisicao> buscarPorId(@PathVariable String id) {
        Aquisicao aquisicao = aquisicaoService.buscarPorId(id);
        return ResponseEntity.ok(aquisicao);
    }
}