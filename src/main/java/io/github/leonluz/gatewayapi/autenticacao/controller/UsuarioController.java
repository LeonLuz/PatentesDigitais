package io.github.leonluz.gatewayapi.autenticacao.controller;

import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.service.NITService;
import io.github.leonluz.gatewayapi.autenticacao.service.OrganizacaoService;
import io.github.leonluz.gatewayapi.autenticacao.service.PesquisadorService;
import io.github.leonluz.gatewayapi.autenticacao.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    private UsuarioService usuarioService;
    private NITService nitService;
    private PesquisadorService pesquisadorService;
    private OrganizacaoService organizacaoService;


    public UsuarioController(UsuarioService usuarioService,NITService nitService,
                             PesquisadorService pesquisadorService,
                             OrganizacaoService organizacaoService) {

        this.usuarioService = usuarioService;
        this.nitService = nitService;
        this.pesquisadorService = pesquisadorService;
        this.organizacaoService = organizacaoService;
    }

    @PostMapping("/nit")
    public NIT salvarNIT(@RequestBody NITRequestDTO dto) {
        return this.nitService.salvarNit(dto);
    }

    @PostMapping("/pesquisador")
    public Pesquisador salvarPesquisador(@RequestBody Pesquisador pesquisador) {
        return this.pesquisadorService.salvarPesquisador(pesquisador);
    }

    @PostMapping("/organizacao")
    public Organizacao salvarOrganizacao(@RequestBody Organizacao organizacao) {
        return this.organizacaoService.salvarOrganizacao(organizacao);
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
    public Pesquisador atualizarPesquisador(@PathVariable("id") String id, @RequestBody Pesquisador pesquisador) {
        return pesquisadorService.atualizarPesquisador(id, pesquisador);
    }

    @PutMapping("/organizacao/{id}")
    public Organizacao atualizarOrganizacao(@PathVariable("id") String id, @RequestBody Organizacao organizacao) {
        return organizacaoService.atualizarOrganizacao(id, organizacao);
    }

    @GetMapping("/nit/buscar")
    public NIT buscarRazaoSocialNit(@RequestParam("razaoSocial") String razaoSocial) {
        return nitService.findByRazaoSocial(razaoSocial);
    }
}
