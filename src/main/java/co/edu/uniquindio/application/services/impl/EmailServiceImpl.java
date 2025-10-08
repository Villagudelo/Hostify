package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.exceptions.ValidationException;
import co.edu.uniquindio.application.services.EmailService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public void sendResetCodeEmail(String email, String code) throws ValidationException {
        log.info("Intentando enviar código de recuperación a: {}", email);
        emailValidator(email);

        try {
            byte[] pdfBytes = generateResetCodePdf(email, code);

            sendEmailWithAttachment(
                    email,
                    "Código de Recuperación de Contraseña - Hostify ",
                    "Adjunto encontrarás el código de recuperación de contraseña. Este código es válido por 15 minutos.",
                    "CodigoRecuperacion.pdf",
                    pdfBytes
            );

            log.info(" Email de recuperación enviado exitosamente a: {}", email);

        } catch (Exception e) {
            log.error(" Error enviando email a {}: {}", email, e.getMessage());
            throw new ValidationException("Error al enviar el email de recuperación: " + e.getMessage());
        }
    }

    /**
     * Validar el formato del email
     */
    private void emailValidator(String email) throws ValidationException {
        if (email == null || email.isEmpty()) {
            throw new ValidationException("El email no puede estar vacío");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Formato de email inválido: " + email);
        }
    }

    /**
     * Enviar el email con archivo adjunto
     */
    private void sendEmailWithAttachment(String to, String subject, String text,
                                         String attachmentName, byte[] attachment) throws Exception {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            helper.setFrom("noreply@alojamientosapp.com");

            // Adjuntar PDF
            ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment, "application/pdf");
            helper.addAttachment(attachmentName, dataSource);

            mailSender.send(message);
            log.info(" Email enviado correctamente a: {}", to);

        } catch (Exception e) {
            log.error(" Falló el envío de email: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Genera un PDF para el código de recuperación
     */
    private byte[] generateResetCodePdf(String email, String code) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Contenido del PDF
            document.add(new Paragraph("RECUPERACIÓN DE CONTRASEÑA - ALOJAMIENTOS APP")
                    .setBold().setFontSize(16));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Fecha: " + LocalDateTime.now().format(formatter)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Estimado usuario,"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Has solicitado restablecer tu contraseña. Usa el siguiente código:"));
            document.add(new Paragraph(" "));

            // Código destacado
            document.add(new Paragraph("CÓDIGO DE VERIFICACIÓN:")
                    .setBold());
            document.add(new Paragraph(code)
                    .setFontSize(24)
                    .setBold()
                    .setMarginTop(10)
                    .setMarginBottom(10));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Email: " + email));
            document.add(new Paragraph("Válido hasta: " + expirationTime.format(formatter)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Instrucciones:")
                    .setBold());
            document.add(new Paragraph("1. Ingresa este código en la aplicación"));
            document.add(new Paragraph("2. Crea una nueva contraseña"));
            document.add(new Paragraph("3. El código expira en 15 minutos"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" Si no solicitaste este cambio, ignora este mensaje."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Atentamente,"));
            document.add(new Paragraph("El equipo de Alojamientos App"));
        }

        return outputStream.toByteArray();
    }

    @Override
    public void sendNewCommentNotification(String hostEmail, String guestName, String placeTitle, String commentText, int rating) {
        log.info("Intentando enviar notificación de nuevo comentario a: {}", hostEmail);
        try {
            emailValidator(hostEmail);

            String subject = "Nuevo comentario en tu alojamiento";
            String body = String.format(
                "¡Hola!\n\n%s ha dejado un nuevo comentario en tu alojamiento \"%s\":\n\n\"%s\"\n\nCalificación: %d estrellas.\n\nIngresa a Hostify para responder el comentario.",
                guestName, placeTitle, commentText, rating
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(hostEmail);
            helper.setSubject(subject);
            helper.setText(body);
            helper.setFrom("noreply@alojamientosapp.com");

            mailSender.send(message);
            log.info("Notificación de comentario enviada correctamente a: {}", hostEmail);

        } catch (Exception e) {
            log.error("Falló el envío de notificación de comentario: {}", e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }
}