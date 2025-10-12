package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired; // Keep this import, it's good practice
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import Ravindra.Stores.Ravindra_Stores_backend.util.JwtUtil;


@RestController
@RequestMapping("/api/auth")
public class UserController {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VerificationTokenRepository tokenRepo;

    
    @Autowired
    private PasswordChangeTokenRepository passwordTokenRepo;

    @Autowired
    private EmailService emailService;

    

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getGmail(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getGmail());

        final User user = userRepo.findByGmail(authenticationRequest.getGmail()).orElseThrow(() -> new Exception("User not found"));

        if (!user.isVerified()) {
            return ResponseEntity.status(403).body("Please verify your email address before logging in. Check your email for the verification link.");
        }
        
        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Your account is disabled. Please contact support.");
        }

        final String jwt = jwtUtil.generateToken(userDetails);
        final String userNickname = user.getUsername();
        final String gmail = user.getGmail();
        final String role = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_CUSTOMER");

        return ResponseEntity.ok(new LoginResponse(user.getId(), userNickname, gmail, role, jwt, user.getPicture(), user.getFullName(), user.getAddressLine1(), user.getAddressLine2(), user.getCity(), user.getPostalCode(), user.getTelephone()));
    }
    
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleToken googleToken) {
        try {
            String url = "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(googleToken.getToken()); 
            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUserInfo.class);
            GoogleUserInfo userInfo = response.getBody();

            if (userInfo == null) {
                return ResponseEntity.status(400).body("Invalid Google token or unable to fetch user info");
            }

            Optional<User> existingUser = userRepo.findByGmail(userInfo.getEmail());

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                if (!user.isVerified() || !user.isEnabled()) {
                    return ResponseEntity.status(403).body("Your account is not active. Please verify your email or contact support.");
                }
                
                user.setPicture(userInfo.getPicture());
                userRepo.save(user);

                final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getGmail());
                final String jwt = jwtUtil.generateToken(userDetails);
                final String userNickname = user.getUsername();
                final String gmail = user.getGmail();
                final String role = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_CUSTOMER");

                return ResponseEntity.ok(new LoginResponse(user.getId(), userNickname, gmail, role, jwt, user.getPicture(), user.getFullName(), user.getAddressLine1(), user.getAddressLine2(), user.getCity(), user.getPostalCode(), user.getTelephone()));

            } else {
               return ResponseEntity.status(404).body("User not found. Please register first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred during Google authentication.");
        }
    }


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@RequestBody User newUser) {
        System.out.println("--- Inside register method ---");
        System.out.println("Received user data: " + newUser.toString());

        Optional<User> existingUserOpt = userRepo.findByGmail(newUser.getGmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isVerified()) {
                System.out.println("User is already verified. Returning 409.");
                return ResponseEntity.status(409).body("This email address is already registered and verified.");
            } else {
                System.out.println("User exists but is not verified. Cleaning up old records.");
                VerificationToken oldToken = tokenRepo.findByUser(existingUser);
                if (oldToken != null) {
                    tokenRepo.delete(oldToken);
                }
                userRepo.delete(existingUser);
                System.out.println("Old user and token deleted. Proceeding with new registration.");
            }
        }

        System.out.println("Setting user properties (verified=false, enabled=false)...");
        newUser.setVerified(false);
        newUser.setEnabled(false);
        newUser.setRole("ROLE_CUSTOMER");

        System.out.println("Encoding password...");
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        System.out.println("Saving new user...");
        User savedUser = userRepo.save(newUser);
        System.out.println("New user saved.");

        System.out.println("Generating verification token...");
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        
        System.out.println("Saving verification token...");
        tokenRepo.save(verificationToken);
        System.out.println("Verification token saved.");

        System.out.println("Sending verification email...");
        try {
            emailService.sendVerificationEmail(savedUser, token);
            System.out.println("Verification email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(newUser.getUsername() + " you registered successfully. Note: There was an issue sending the verification email. Please contact support.");
        }

        System.out.println("Returning 200 OK.");
        return ResponseEntity.ok(newUser.getUsername() + " you registered successfully. Please check your email for verification.");
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepo.findByToken(token);
        if (verificationToken == null) {
            return ResponseEntity.status(400).body("Invalid token");
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            tokenRepo.delete(verificationToken);
            return ResponseEntity.status(400).body("Token expired");
        }

        user.setVerified(true);
        user.setEnabled(true);
        userRepo.save(user);
        
        tokenRepo.delete(verificationToken);

        return ResponseEntity.ok("User verified successfully");
    }

    
    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepo.findByGmail(request.getEmail());
        
        if (!userOptional.isPresent()) {
            return ResponseEntity.ok("If the email address exists in our system, you will receive a password reset link.");
        }
        
        User user = userOptional.get();
        
        PasswordChangeToken existingToken = passwordTokenRepo.findByUser(user);
        if (existingToken != null) {
            passwordTokenRepo.delete(existingToken);
            System.out.println("Deleted existing password reset token for user: " + user.getGmail());
        }
        
        String token = UUID.randomUUID().toString();
        PasswordChangeToken passwordResetToken = new PasswordChangeToken(token, user);
        passwordTokenRepo.save(passwordResetToken);
        
    
        try {
            emailService.sendPasswordResetEmail(user, token);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to send password reset email. Please try again later.");
        }
        
        return ResponseEntity.ok("If the email address exists in our system, you will receive a password reset link.");
    }
    

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            return ResponseEntity.status(400).body("Reset token is required");
        }
        
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty() || request.getNewPassword().length() < 6) {
            return ResponseEntity.status(400).body("New password is required and must be at least 6 characters long");
        }
        
        
        PasswordChangeToken passwordResetToken = passwordTokenRepo.findByToken(request.getToken());
        
        if (passwordResetToken == null) {
            return ResponseEntity.status(400).body("Invalid reset token. Please request a new password reset.");
        }
        
        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            passwordTokenRepo.delete(passwordResetToken); 
            return ResponseEntity.status(400).body("Reset token has expired. Please request a new password reset.");
        }
        
        User user = passwordResetToken.getUser();
        
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
        
        
        passwordTokenRepo.delete(passwordResetToken);
        
        System.out.println("Password reset successful for user: " + user.getGmail());
        
        return ResponseEntity.ok("Password has been reset successfully. You can now login with your new password.");
    }
}

class ForgotPasswordRequest {
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class ResetPasswordRequest {
    private String token;
    private String newPassword;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
