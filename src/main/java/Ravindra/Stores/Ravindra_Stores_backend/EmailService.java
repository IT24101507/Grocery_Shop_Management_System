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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getGmail());
        message.setSubject("Welcome to Ravindra Stores!");
        message.setText("To verify your account, please click the link below:\n\n" 
                + "http://localhost:3002/verify?token=" + token);
        mailSender.send(message);

    }
}
