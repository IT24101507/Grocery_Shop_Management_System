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
            System.out.println("Starting to send verification email to: " + user.getGmail());
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getGmail());
            message.setSubject("Welcome to Ravindra Stores!");
            message.setText("Dear " + user.getUsername() + ",\n\n" +
                    "Welcome to Ravindra Stores! To verify your account, please click the link below:\n\n" 
                    + "http://localhost:3002/verify?token=" + token + "\n\n" +
                    "Best regards,\nRavindra Stores Team");
            
            System.out.println("Email message prepared. Sending...");
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + user.getGmail());
            
        } catch (Exception e) {
            System.err.println("Error sending email to " + user.getGmail() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
