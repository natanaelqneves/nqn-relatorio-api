package com.nqn.relatorios_semob.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "O nome do usuário é obrigatório.")
        String nomeCompleto,

        @NotBlank(message = "A matricula do usuário é obrigatória.")
        String matricula,

        @NotBlank(message = "O nome de login de usuário é obrigatório.")
        String nomeDeUsuario,

        @NotBlank(message = "A senha é obrigatório.")
        String senha,

        @NotBlank(message = "O e-mail pessoal/funcional é obrigatório.")
        @Email(message = "O e-mail informado deve ser válido.")
        @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres.")
        String email,

        @NotBlank(message = "O e-mail de disparo SMTP é obrigatório.")
        @Email(message = "O e-mail de disparo informado deve ser válido.")
        @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres.")
        String emailSmtp,

        @NotBlank(message = "A senha do Email para envio de relatórios é obrigatório.")
        String senhaSmtp,

        //@NotBlank(message = "A assinatura do agente é  necessária para o envio de relatórios.")
        String assinatura
) {
}
