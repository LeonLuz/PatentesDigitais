package io.github.leonluz.gatewayapi.autenticacao.service;

import io.github.leonluz.gatewayapi.autenticacao.model.Usuario;
import io.github.leonluz.gatewayapi.autenticacao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario obterUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public void deletarUsuarioPorId(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado para exclusão.");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void adicionarAssociado(String idInstituicao, String idUsuarioAssociado) {
        // 1. Validar se a Instituição (NIT ou Organização) existe no banco de dados
        if (!usuarioRepository.existsById(idInstituicao)) {
            throw new IllegalArgumentException("Instituição não encontrada com o ID fornecido.");
        }

        // 2. Validar se o Usuário a ser associado existe no banco de dados
        if (!usuarioRepository.existsById(idUsuarioAssociado)) {
            throw new IllegalArgumentException("Usuário a ser associado não encontrado.");
        }

        // 3. Verificar se o vínculo já existe para impedir duplicados
        if (usuarioRepository.existsVinculo(idInstituicao, idUsuarioAssociado) > 0) {
            throw new IllegalStateException("Este usuário já se encontra associado a esta instituição.");
        }

        // 4. Persistir o vínculo na tabela intermediária
        usuarioRepository.adicionarVinculo(idInstituicao, idUsuarioAssociado);
    }

    @Transactional
    public void excluirAssociado(String idInstituicao, String idUsuarioAssociado) {
        // 1. Validar se o vínculo realmente existe antes de tentar a exclusão
        if (usuarioRepository.existsVinculo(idInstituicao, idUsuarioAssociado) == 0) {
            throw new IllegalArgumentException("Não foi encontrado nenhum vínculo entre a instituição e o usuário informados.");
        }

        // 2. Remover o vínculo da tabela intermediária
        usuarioRepository.removerVinculo(idInstituicao, idUsuarioAssociado);
    }
}