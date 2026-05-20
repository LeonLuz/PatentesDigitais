package io.github.leonluz.gatewayapi.autenticacao.controller;

import io.github.leonluz.gatewayapi.autenticacao.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

   
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // 1. adicionar usuário associado
    @PostMapping("/{idInstituicao}/associados")
    public ResponseEntity<String> adicionarUsuarioAssociado(
            @PathVariable String idInstituicao, 
            @RequestBody String idUsuarioParaVincular) { 
        
        usuarioService.adicionarAssociado(idInstituicao, idUsuarioParaVincular);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário associado com sucesso à instituição.");
    }

    // excluir usuário associado
    @DeleteMapping("/{idInstituicao}/associados/{idUsuarioAssociado}")
    public ResponseEntity<String> excluirUsuarioAssociado(
            @PathVariable String idInstituicao, 
            @PathVariable String idUsuarioAssociado) {
        
        usuarioService.excluirAssociado(idInstituicao, idUsuarioAssociado);
        
        return ResponseEntity.ok("Vínculo do usuário removido com sucesso.");
    }
}