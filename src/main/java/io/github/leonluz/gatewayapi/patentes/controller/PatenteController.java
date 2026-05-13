package io.github.leonluz.gatewayapi.patentes.controller;

import io.github.leonluz.gatewayapi.patentes.model.Patente;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("patentes")
public class PatenteController {

    @PostMapping
    public void salvar(@RequestBody Patente patente) {
        System.out.println("Patente recebida: " + patente);
    }
}
