package io.github.leonluz.gatewayapi.pedidos.controller;

import io.github.leonluz.gatewayapi.pedidos.dto.AquisicaoRequestDTO;
import io.github.leonluz.gatewayapi.pedidos.model.Aquisicao;
import io.github.leonluz.gatewayapi.pedidos.service.AquisicaoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aquisicoes")
public class AquisicaoController {

    private final AquisicaoService aquisicaoService;

    public AquisicaoController(AquisicaoService aquisicaoService) {
        this.aquisicaoService = aquisicaoService;
    }

    @PostMapping
    public Aquisicao criarAquisicao(@RequestBody AquisicaoRequestDTO dto) {
        return aquisicaoService.criarAquisicao(dto);
    }

    @GetMapping("/{id}")
    public Aquisicao buscarPorId(@PathVariable String id) {
        return aquisicaoService.buscarPorId(id);
    }
}