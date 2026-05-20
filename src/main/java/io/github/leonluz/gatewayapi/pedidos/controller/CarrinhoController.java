package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.dto.CarrinhoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Carrinho;
import io.github.leonluz.gatewayapi.pedidos.service.CarrinhoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carrinhos")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @PostMapping
    public Carrinho adicionarItem(@RequestBody CarrinhoRequestDTO dto) {
        return carrinhoService.adicionarItem(dto);
    }

    @GetMapping("/usuario/{id}")
    public Carrinho buscarPorUsuario(@PathVariable("id") String idUsuario) {
        return carrinhoService.buscarPorUsuario(idUsuario);
    }
}