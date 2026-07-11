package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.AutenticacaoDTO;
import com.nqn.relatorios_semob.repository.UsuarioRepository;
import com.nqn.relatorios_semob.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String login(AutenticacaoDTO dto){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.nomeDeUsuario(),
                            dto.senha())
            );
        } catch (DisabledException e) {
            throw new RuntimeException("Acesso pendente de liberação pelo Administrador/DBA");

        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new RuntimeException("Credenciais de acesso incorretas ou usuário não encontrado.");
        }

        var usuario = usuarioRepository.findByNomeDeUsuario(dto.nomeDeUsuario())
                .orElseThrow(() -> new RuntimeException("Erro interno ao carregar dados do usuário."));

        return jwtService.gerarToken(usuario);
    }
}


