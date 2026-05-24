package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping("/{idUsuario}/itens")
    public ResponseEntity<String> adicionar(
            @PathVariable UUID idUsuario,
            @RequestBody UUID idPatente) {

        carrinhoService.adicionarAoCarrinho(idUsuario, idPatente);
        return ResponseEntity.status(HttpStatus.CREATED).body("Patente adicionada ao carrinho de compras.");
    }

    @DeleteMapping("/{idUsuario}/itens/{idPatente}")
    public ResponseEntity<String> remover(
            @PathVariable UUID idUsuario,
            @PathVariable UUID idPatente) {

        carrinhoService.removerDoCarrinho(idUsuario, idPatente);
        return ResponseEntity.ok("Patente removida do carrinho.");
        }
    }