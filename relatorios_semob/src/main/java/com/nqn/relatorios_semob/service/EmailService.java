package com.nqn.relatorios_semob.service;


import com.nqn.relatorios_semob.dto.EmailRecuperacaoDTO;
import com.nqn.relatorios_semob.model.Relatorio;
import com.nqn.relatorios_semob.model.Usuario;
import com.nqn.relatorios_semob.repository.RelatorioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailService {

    @Value("${email.destinatario}")
    private final String emailDestinatario;
    private final JavaMailSender mailSender;
    private final RelatorioRepository relatorioRepository;
    private final DocxService docxService;
    private final ZipService zipService;
    private final CriptografiaUtil criptografiaUtil;
    private final UsuarioService usuarioService;

    public EmailService(String emailDestinatario, JavaMailSender mailSender,
                        RelatorioRepository relatorioRepository, DocxService docxService,
                        ZipService zipService, CriptografiaUtil criptografiaUtil,
                        UsuarioService usuarioService) {

        this.emailDestinatario = emailDestinatario;
        this.mailSender = mailSender;
        this.relatorioRepository = relatorioRepository;
        this.docxService = docxService;
        this.zipService = zipService;
        this.criptografiaUtil = criptografiaUtil;
        this.usuarioService = usuarioService;
    }

    @Async
    public void enviarRelatoriosZip(
            List<Long> idsRelatorios,
            Usuario usuarioLogado) {

        try {
            List<Relatorio> relatorios = relatorioRepository.findAllById(idsRelatorios);
            Map<String, byte[]> arquivosDocx = new HashMap<>();

            //Gera o DOCX de cada relatório
            for (Relatorio r : relatorios) {
                byte[] docxBytes = docxService.gerarDocx(r);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String nomeArquivo = "Relatorio_" + r.getUsuario().getNomeCompleto() + "_" + r.getDataDoServico().format(formatter) +"_"+ turno(r) + ".docx";
                arquivosDocx.put(nomeArquivo, docxBytes);
            }

            byte[] zipBytes = zipService.compactarArquivos(arquivosDocx);


            JavaMailSenderImpl dinamicMailSender = new JavaMailSenderImpl();
            dinamicMailSender.setHost("smtp.gmail.com");
            dinamicMailSender.setPort(587);

            dinamicMailSender.setUsername(usuarioLogado.getEmailSmtp());   // ex: agente.nael@gmail.com
            String senhaSmtpOriginal = criptografiaUtil.descriptografar(usuarioLogado.getSenhaAppSmtp());
            dinamicMailSender.setPassword(senhaSmtpOriginal);

            Properties props = dinamicMailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "false");

            MimeMessage message = dinamicMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String mesTraduzido = relatorios.get(0).getDataDoServico().getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            String mes = mesTraduzido.substring(0, 1).toUpperCase() + mesTraduzido.substring(1);

            helper.setFrom(usuarioLogado.getEmailSmtp());
            helper.setTo(usuarioLogado.getEmail());//emailDestinatario
            helper.setSubject("Relatórios de Serviço do mês " + mes + ".");
            helper.setText("<p>Olá,</p><p>Seguem em anexo os relatórios de serviço do mês de " + mes + ".</p>", true);


            String nomeZip = "Relatorios_" + relatorios.get(0).getUsuario().getNomeCompleto().replace(" ", "_") + "_" + mes.toUpperCase() + ".zip";
            helper.addAttachment(nomeZip, new ByteArrayResource(zipBytes));

            dinamicMailSender.send(message);
            System.out.println("E-mail enviado com sucesso via SMTP do usuário: " + usuarioLogado.getEmailSmtp());

        } catch (Exception e) {
            System.err.println("Falha ao processar ou enviar lote por e-mail: " + e.getMessage());
        }
    }

    private String turno(Relatorio relatorio) {
        if(relatorio.getHoraIncio() < 19){
            return "Diurno";
        }
        return "Noturno";
    }
}

//    public void lembrarSenha(EmailRecuperacaoDTO dto) {
//        Usuario usuario = usuarioService.buscarPorEmail(dto.email()).
//                orElseThrow(() -> new EntityNotFoundException("Conta não encontrada para o email: " + dto.email()));
//
//        JavaMailSenderImpl dinamicMailSender = new JavaMailSenderImpl();
//        dinamicMailSender.setHost("smtp.gmail.com");
//        dinamicMailSender.setPort(587);
//
//        dinamicMailSender.setUsername("usuarioLogado.getEmailSmtp()");
//        String senhaSmtpOriginal = criptografiaUtil.descriptografar("usuarioLogado.getSenhaAppSmtp()");
//        dinamicMailSender.setPassword(senhaSmtpOriginal);
//
//        Properties props = dinamicMailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "false");
//
//        MimeMessage message = dinamicMailSender.createMimeMessage();
//        MimeMessageHelper helper = null;
//
//        try {
//            helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setFrom("usuarioLogado.getEmailSmtp()");
//            helper.setTo(emailDestinatario);
//            helper.setSubject("Recuperação de Senha");
//            helper.setText("<p>Olá,</p><p>A sua senha é: " + usuario.getPassword() + ".</p>", true);
//
//            dinamicMailSender.send(message);
//
//        } catch (MessagingException ex) {
//            throw new RuntimeException(ex);
//        } catch(Exception e){
//        System.err.println("Falha ao processar ou enviar lote por e-mail: " + e.getMessage());
//    }
//}
