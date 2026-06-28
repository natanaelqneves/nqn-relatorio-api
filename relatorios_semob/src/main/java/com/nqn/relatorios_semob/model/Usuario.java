package com.nqn.relatorios_semob.model;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "matricula", nullable = false, unique = true)
    private String matricula;

    @Column(name = "nome_de_usuario", nullable = false, unique = true)
    private String nomeDeUsuario;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "email", nullable = false)
    private String email;

    // 🚨 NOVOS CAMPOS PARA O DISPARO INDIVIDUAL
    @Column(name = "email_smtp")
    private String emailSmtp;       // O e-mail que vai disparar (ex: agente.nael@gmail.com)

    @Column(name = "senha_smtp")
    private String senhaAppSmtp;    // A "Senha de App" criptografada ou mascarada

    @Column(name = "assinatura", length = 500)
    private String assinatura;

    public Usuario() {
    }

    public Usuario(Long id, String nomeCompleto, String matricula, String nomeDeUsuario, String senha, String email, String emailSmtp, String senhaAppSmtp, String assinatura) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.matricula = matricula;
        this.nomeDeUsuario = nomeDeUsuario;
        this.senha = senha;
        this.email = email;
        this.emailSmtp = emailSmtp;
        this.senhaAppSmtp = senhaAppSmtp;
        this.assinatura = assinatura;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNomeDeUsuario() {
        return nomeDeUsuario;
    }

    public void setNomeDeUsuario(String nomeDeUsuario) {
        this.nomeDeUsuario = nomeDeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailSmtp() {
        return emailSmtp;
    }

    public void setEmailSmtp(String emailSmtp) {
        this.emailSmtp = emailSmtp;
    }

    public String getSenhaAppSmtp() {
        return senhaAppSmtp;
    }

    public void setSenhaAppSmtp(String senhaAppSmtp) {
        this.senhaAppSmtp = senhaAppSmtp;
    }

    public String getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(String assinatura) {
        this.assinatura = assinatura;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 🛠️ Garante que o Spring Security reconheça o usuário como autenticado e ativo
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.nomeDeUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

