package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private String loadEmailTemplate(String templateName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/templates/" + templateName)));
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(User user, String token) {
        try {
            String verificationLink = "http://localhost:3002/verify?token=" + token;
            String htmlContent = loadEmailTemplate("verification-email.html");
            htmlContent = htmlContent.replace("{{verification_link}}", verificationLink);
            sendHtmlEmail(user.getGmail(), "Welcome to Ravindra Stores!", htmlContent);
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(User user, String token) {
        try {
            String resetLink = "http://localhost:3002/reset-password?token=" + token;
            String htmlContent = loadEmailTemplate("password-reset-email.html");
            htmlContent = htmlContent.replace("{{user_name}}", user.getFullName());
            htmlContent = htmlContent.replace("{{reset_link}}", resetLink);
            sendHtmlEmail(user.getGmail(), "Password Reset - Ravindra Stores", htmlContent);
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
