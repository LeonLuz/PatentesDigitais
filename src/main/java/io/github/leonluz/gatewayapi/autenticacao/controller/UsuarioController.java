package io.github.leonluz.gatewayapi.autenticacao.controller;

import io.github.leonluz.gatewayapi.autenticacao.dto.*;
import io.github.leonluz.gatewayapi.autenticacao.model.NIT;
import io.github.leonluz.gatewayapi.autenticacao.model.Organizacao;
import io.github.leonluz.gatewayapi.autenticacao.model.Pesquisador;
import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.service.NITService;
import io.github.leonluz.gatewayapi.autenticacao.service.OrganizacaoService;
import io.github.leonluz.gatewayapi.autenticacao.service.PesquisadorService;
import io.github.leonluz.gatewayapi.autenticacao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> salvarNIT(@Valid @RequestBody NITRequestDTO dto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.nitService.salvarNit(dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pesquisador")
    public ResponseEntity<?> salvarPesquisador(@Valid @RequestBody PesquisadorRequestDTO dto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.pesquisadorService.salvarPesquisador(dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/organizacao")
    public ResponseEntity<?> salvarOrganizacao(@Valid @RequestBody OrganizacaoRequestDTO dto) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(this.organizacaoService.salvarOrganizacao(dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
    public ResponseEntity<?> atualizarNIT(@PathVariable("id") UUID id, @RequestBody NitUpdateDTO dto) {
        try{
            return ResponseEntity.ok(nitService.atualizarNit(id, dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/pesquisador/{id}")
    public ResponseEntity<?> atualizarPesquisador(@PathVariable("id") UUID id, @Valid @RequestBody PesquisadorUpdateDTO dto) {
        try{
            return ResponseEntity.ok(pesquisadorService.atualizarPesquisador(id, dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/organizacao/{id}")
    public ResponseEntity<?> atualizarOrganizacao(@PathVariable("id") UUID id, @RequestBody OrganizacaoUpdateDTO dto) {
        try{
            return ResponseEntity.ok(organizacaoService.atualizarOrganizacao(id, dto));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @GetMapping("/ids")
    public ResponseEntity<List<String>> listarIdUsuarios() {
        return ResponseEntity.ok(usuarioService.listarIdUsuarios());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<String> tratarErrosDeValidacao(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        String mensagem = (fieldError != null) ? fieldError.getDefaultMessage() : "Erro de validação.";
        return ResponseEntity.badRequest().body(mensagem);
    }
}
