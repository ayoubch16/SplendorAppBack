package org.example.back.web;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.back.domain.EmailRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.mail.MessagingException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Objects;
@Transactional
@Slf4j
@AllArgsConstructor
@Builder
@RestController
@RequestMapping("/api/emails")
public class EmailResource {
    private final JavaMailSenderImpl mailSender;


    @PostMapping(value = "/send-with-attachment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendEmailWithAttachment(@RequestBody EmailRequest emailRequest) {
        try {
            // Convertir base64 en byte[]
            byte[] pdfBytes = Base64.getDecoder().decode(emailRequest.getPdfBase64());

            // Créer un MultipartFile temporaire
            MultipartFile pdfFile = new Base64DecodedMultipartFile(
                    pdfBytes,
                    emailRequest.getPdfFileName()
            );

            sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage(), pdfFile);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    // Classe utilitaire pour créer un MultipartFile à partir de byte[]
class Base64DecodedMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;

        public Base64DecodedMultipartFile(byte[] content, String name) {
            this.content = content;
            this.name = name;
        }

        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return name; }
        @Override public String getContentType() { return "application/pdf"; }
        @Override public boolean isEmpty() { return content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException { Files.write(dest.toPath(), content); }
    }


    private void sendEmail(String to, String subject, String text, MultipartFile attachment)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // Ajouter la pièce jointe
        helper.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()),
                attachment,
                Objects.requireNonNull(attachment.getContentType()));

        mailSender.send(message);
    }


    @PostMapping(value = "/send-simple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendSimpleEmail(
            @RequestBody SimpleEmailRequest request) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getMessage(), false); // false = texte non HTML

            mailSender.send(message);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    // Classe DTO pour la requête
    public static class SimpleEmailRequest {
        private String to;
        private String subject;
        private String message;

        // Getters et Setters obligatoires
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
