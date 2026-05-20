package io.github.leonluz.gatewayapi.autenticacao.controller;

import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.service.NITService;
import io.github.leonluz.gatewayapi.autenticacao.service.OrganizacaoService;
import io.github.leonluz.gatewayapi.autenticacao.service.PesquisadorService;

import io.github.leonluz.gatewayapi.autenticacao.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final NITService nitService;
    private final PesquisadorService pesquisadorService;
    private final OrganizacaoService organizacaoService;


    public UsuarioController(UsuarioService usuarioService,NITService nitService,
                             PesquisadorService pesquisadorService,
                             OrganizacaoService organizacaoService) {

   
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
    @PostMapping("/nit")
    public NIT salvarNIT(@RequestBody NITRequestDTO dto) {
        return this.nitService.salvarNit(dto);
    }

    @PostMapping("/pesquisador")
    public Pesquisador salvarPesquisador(@RequestBody PesquisadorRequestDTO dto) {
        return this.pesquisadorService.salvarPesquisador(dto);
    }

    @PostMapping("/organizacao")
    public Organizacao salvarOrganizacao(@RequestBody OrganizacaoRequestDTO dto) {
        return this.organizacaoService.salvarOrganizacao(dto);
    }

    @GetMapping("/{id}")
    public Usuario obterUsuarioPorId(@PathVariable("id") String id) {
        return usuarioService.obterUsuarioPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarUsuarioPorId(@PathVariable("id") String id) {
        usuarioService.deletarUsuarioPorId(id);
    }

    @PutMapping("/nit/{id}")
    public NIT atualizarNIT(@PathVariable("id") String id, @RequestBody NITRequestDTO dto) {
        return nitService.atualizarNit(id, dto);
    }

    @PutMapping("/pesquisador/{id}")
    public Pesquisador atualizarPesquisador(@PathVariable("id") String id, @RequestBody PesquisadorRequestDTO dto) {
        return pesquisadorService.atualizarPesquisador(id, dto);
    }

    @PutMapping("/organizacao/{id}")
    public Organizacao atualizarOrganizacao(@PathVariable("id") String id, @RequestBody OrganizacaoRequestDTO dto) {
        return organizacaoService.atualizarOrganizacao(id, dto);
    }

    @GetMapping("/nit/buscar")
    public NIT buscarRazaoSocialNit(@RequestParam("razaoSocial") String razaoSocial) {
        return nitService.findByRazaoSocial(razaoSocial);
    }

    @GetMapping("/pesquisador/buscar")
    public Pesquisador buscarPorNome(@RequestParam("nome") String nome) {
        return pesquisadorService.findByNome(nome);
    }
}
