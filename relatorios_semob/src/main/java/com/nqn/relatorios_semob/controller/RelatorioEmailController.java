package com.nqn.relatorios_semob.controller;

import com.nqn.relatorios_semob.dto.EnvioLoteEmailDTO;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class RelatorioEmailController {

    private final EmailService emailService;

    public RelatorioEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> enviarLoteZip(
            @Valid @RequestBody EnvioLoteEmailDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        emailService.enviarRelatoriosZip(dto.getIdsRelatorios(), usuarioLogado);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Os relatórios serão enviados .");
    }

    @PostMapping("/baixar-lote")
    public ResponseEntity<byte[]> baixarRelatoriosZip(
            @Valid @RequestBody EnvioLoteEmailDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        byte[] relatoriosZip = emailService.baixarRelatoriosZip(dto.getIdsRelatorios(), usuarioLogado);

        String nomeDoZip = "Relatorios_" + usuarioLogado.getNomeCompleto() +".zip";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeDoZip + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("application/zip"))
                .contentLength(relatoriosZip.length)
                .body(relatoriosZip);
    }
}
