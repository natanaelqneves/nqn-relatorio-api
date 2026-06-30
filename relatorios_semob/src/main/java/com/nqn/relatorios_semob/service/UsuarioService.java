package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.AssinaturaDTO;
import com.nqn.relatorios_semob.dto.MudarSenhaDTO;
import com.nqn.relatorios_semob.dto.UsuarioRequestDTO;
import com.nqn.relatorios_semob.dto.UsuarioResponseDTO;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CriptografiaUtil criptografiaUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CriptografiaUtil criptografiaUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.criptografiaUtil = criptografiaUtil;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto){

        if(buscarPorNomeDeUsuario(dto.nomeDeUsuario()).isPresent()) {
            throw  new IllegalArgumentException("Nome de Usuário indisponível.");
        }

        if(buscarPorMatricula(dto.matricula()).isPresent()) {
            throw  new IllegalArgumentException("Matrícula já cadastrada.");
        }

        if(buscarPorEmail(dto.email()).isPresent()) {
            throw  new IllegalArgumentException("Email já cadastrado.");
        }

        if(buscarPorEmail(dto.emailSmtp()).isPresent()) {
            throw  new IllegalArgumentException("Email para envio de relatórios já cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha().trim());

        String senhaSmtpCriptografada = criptografiaUtil.criptografar(dto.senhaSmtp());

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.nomeCompleto().trim());
        usuario.setMatricula(dto.matricula().trim());
        usuario.setNomeDeUsuario(dto.nomeDeUsuario().trim());
        usuario.setSenha(senhaCriptografada);
        usuario.setEmail(dto.email().trim());
        usuario.setEmailSmtp(dto.emailSmtp().trim());
        usuario.setSenhaAppSmtp(senhaSmtpCriptografada);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        UsuarioResponseDTO  response= new UsuarioResponseDTO(
                usuarioSalvo.getNomeCompleto(),
                usuarioSalvo.getMatricula(),
                usuarioSalvo.getNomeDeUsuario(),
                usuarioSalvo.getEmail(),
                usuario.getAssinatura());
        return response;
    }

    @Transactional
    public void salvarAssinatura(
            Usuario usuarioLogado,
            AssinaturaDTO dto) {

        Usuario usuario = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    usuario.setAssinatura(dto.assinatura());
    }

    private Optional<Usuario> buscarPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorNomeDeUsuario(String nomeDeUsuario) {
        return usuarioRepository.findByNomeDeUsuario(nomeDeUsuario);
    }

    @Transactional
    public UsuarioResponseDTO mudarSenha(Long id, MudarSenhaDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        String senhaCriptografada = passwordEncoder.encode(dto.senha().trim());

        usuario.setSenha(senhaCriptografada);

        UsuarioResponseDTO  response = new UsuarioResponseDTO(
                usuario.getNomeCompleto(),
                usuario.getMatricula(),
                usuario.getNomeDeUsuario(),
                usuario.getEmail(),
                usuario.getAssinatura());

        return response;
    }
}