package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(User user, String token){
        try {
            System.out.println("=== SENDING VERIFICATION EMAIL ===");
            System.out.println("To: " + user.getGmail());
            System.out.println("From: " + fromEmail);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getGmail());
            message.setSubject("Welcome to Ravindra Stores!");
            message.setText("To verify your account, please click the link below:\n\n" 
                    + "http://localhost:3002/verify?token=" + token);
            
            mailSender.send(message);
            System.out.println("Verification email sent successfully!");
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(User user, String token){
        try {
            System.out.println("=== SENDING PASSWORD RESET EMAIL ===");
            System.out.println("To: " + user.getGmail());
            System.out.println("From: " + fromEmail);
            System.out.println("Token: " + token);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getGmail());
            message.setSubject("Password Reset - Ravindra Stores");
            message.setText("Hello " + user.getFullName() + ",\n\n" +
                    "You have requested to reset your password for your Ravindra Stores account.\n\n" +
                    "Please click the link below to reset your password:\n\n" +
                    "http://localhost:3002/reset-password?token=" + token + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "IMPORTANT: If you request another password reset, this link will become invalid.\n" +
                    "Always use the most recent password reset email.\n\n" +
                    "If you did not request this password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Ravindra Stores Team");
            
            mailSender.send(message);
            System.out.println("Password reset email sent successfully!");
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
