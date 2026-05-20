package io.github.leonluz.gatewayapi.pedidos.controller;

<<<<<<< HEAD
import io.github.leonluz.gatewayapi.pedidos.service.CarrinhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
=======
import io.github.leonluz.gatewayapi.pedidos.dto.CarrinhoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.service.CarrinhoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carrinhos")
>>>>>>> main
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

<<<<<<< HEAD
    @PostMapping("/{idUsuario}/itens")
    public ResponseEntity<String> adicionar(
            @PathVariable String idUsuario,
            @RequestBody String idPatente) {
        
        carrinhoService.adicionarAoCarrinho(idUsuario, idPatente);
        return ResponseEntity.status(HttpStatus.CREATED).body("Patente adicionada ao carrinho de compras.");
    }

    @DeleteMapping("/{idUsuario}/itens/{idPatente}")
    public ResponseEntity<String> remover(
            @PathVariable String idUsuario,
            @PathVariable String idPatente) {
        
        carrinhoService.removerDoCarrinho(idUsuario, idPatente);
        return ResponseEntity.ok("Patente removida do carrinho.");
=======
    @PostMapping
    public Carrinho adicionarItem(@RequestBody CarrinhoRequestDTO dto) {
        return carrinhoService.adicionarItem(dto);
    }

    @GetMapping("/usuario/{id}")
    public Carrinho buscarPorUsuario(@PathVariable("id") String idUsuario) {
        return carrinhoService.buscarPorUsuario(idUsuario);
>>>>>>> main
    }
}