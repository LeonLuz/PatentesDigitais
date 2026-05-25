package io.github.leonluz.gatewayapi.autenticacao.controller;

import io.github.leonluz.gatewayapi.autenticacao.dto.LoginRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.NITRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorRequestDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.NitUpdateDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.OrganizacaoUpdateDTO;
import io.github.leonluz.gatewayapi.autenticacao.dto.PesquisadorUpdateDTO;
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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final NITService nitService;
    private final PesquisadorService pesquisadorService;
    private final OrganizacaoService organizacaoService;

    public UsuarioController(UsuarioService usuarioService,
                             NITService nitService,
                             PesquisadorService pesquisadorService,
                             OrganizacaoService organizacaoService) {
        this.usuarioService = usuarioService;
        this.nitService = nitService;
        this.pesquisadorService = pesquisadorService;
        this.organizacaoService = organizacaoService;
    }

    @PostMapping("/{idInstituicao}/associados")
    public ResponseEntity<String> adicionarUsuarioAssociado(
            @PathVariable UUID idInstituicao,
            @RequestBody UUID idUsuarioParaVincular) {

        nitService.adicionarAssociado(idInstituicao, idUsuarioParaVincular);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário associado com sucesso à instituição.");
    }

    @DeleteMapping("/{idInstituicao}/associados/{idUsuarioAssociado}")
    public ResponseEntity<String> excluirUsuarioAssociado(
            @PathVariable UUID idInstituicao,
            @PathVariable UUID idUsuarioAssociado) {

        nitService.excluirAssociado(idInstituicao, idUsuarioAssociado);
        return ResponseEntity.ok("Vínculo do usuário removido com sucesso.");
    }

    @PostMapping("/nit")
    public ResponseEntity<NIT> salvarNIT(@RequestBody NITRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.nitService.salvarNit(dto));
    }

    @PostMapping("/pesquisador")
    public ResponseEntity<Pesquisador> salvarPesquisador(@RequestBody PesquisadorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.pesquisadorService.salvarPesquisador(dto));
    }

    @PostMapping("/organizacao")
    public ResponseEntity<Organizacao> salvarOrganizacao(@RequestBody OrganizacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.organizacaoService.salvarOrganizacao(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obterUsuarioPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.obterUsuarioPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuarioPorId(@PathVariable("id") UUID id) {
        usuarioService.deletarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/nit/{id}")
    public ResponseEntity<NIT> atualizarNIT(@PathVariable("id") UUID id, @RequestBody NitUpdateDTO dto) {
        return ResponseEntity.ok(nitService.atualizarNit(id, dto));
    }

    @PutMapping("/pesquisador/{id}")
    public ResponseEntity<Pesquisador> atualizarPesquisador(@PathVariable("id") UUID id, @RequestBody PesquisadorUpdateDTO dto) {
        return ResponseEntity.ok(pesquisadorService.atualizarPesquisador(id, dto));
    }

    @PutMapping("/organizacao/{id}")
    public ResponseEntity<Organizacao> atualizarOrganizacao(@PathVariable("id") UUID id, @RequestBody OrganizacaoUpdateDTO dto) {
        return ResponseEntity.ok(organizacaoService.atualizarOrganizacao(id, dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody LoginRequestDTO dto) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(dto.email(), dto.senha());
            return ResponseEntity.ok(usuarioAutenticado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/nit/buscar")
    public ResponseEntity<NIT> buscarRazaoSocialNit(@RequestParam("razaoSocial") String razaoSocial) {
        return ResponseEntity.ok(nitService.findByRazaoSocial(razaoSocial));
    }

    @GetMapping("/pesquisador/buscar")
    public ResponseEntity<Pesquisador> buscarPorNome(@RequestParam("nome") String nome) {
        return ResponseEntity.ok(pesquisadorService.findByNome(nome));
    }
}
