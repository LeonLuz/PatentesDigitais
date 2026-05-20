package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.service.AquisicaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
public class AquisicaoController {

  private final AquisicaoService aquisicaoService;

  public AquisicaoController(AquisicaoService aquisicaoService) {
    this.aquisicaoService = aquisicaoService;
  }

  @PostMapping("/{idUsuario}")
  public ResponseEntity<String> realizarCheckout(@PathVariable String idUsuario) {
    String idAquisicao = aquisicaoService.finalizarCheckout(idUsuario);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body("Checkout finalizado com sucesso! ID da Transação: " + idAquisicao);
  }
}