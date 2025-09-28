package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

        // Check if user is verified and enabled
        if (!user.isVerified()) {
            return ResponseEntity.status(403).body("Please verify your email address before logging in. Check your email for the verification link.");
        }
        
        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Your account is disabled. Please contact support.");
        }

        final String jwt = jwtUtil.generateToken(userDetails);
        final String userNickname = user.getUsername(); // Display name
        final String gmail = user.getGmail();           // Email for functionality
        final String role = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_CUSTOMER");

        return ResponseEntity.ok(new LoginResponse(userNickname, gmail, role, jwt, user.getPicture(), user.getFullName(), user.getAddressLine1(), user.getAddressLine2(), user.getCity(), user.getPostalCode(), user.getTelephone()));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        System.out.println("--- Inside register method ---");
        System.out.println("Received user data: " + newUser.toString());

        System.out.println("Checking if user exists...");
        Optional<User> existingUser = userRepo.findByGmail(newUser.getGmail());
        System.out.println("Finished checking if user exists.");

        if (existingUser.isPresent()) {
            System.out.println("User already exists. Returning 409.");
            return ResponseEntity.status(409).body("Gmail already exists");
        }

        System.out.println("Setting user properties (verified=false, enabled=false)...");
        newUser.setVerified(false);
        newUser.setEnabled(false);
        newUser.setRole("ROLE_CUSTOMER");

        System.out.println("Encoding password...");
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        System.out.println("Saving new user...");
        userRepo.save(newUser);
        System.out.println("New user saved.");

        System.out.println("Generating verification token...");
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, newUser);
        System.out.println("Saving verification token...");
        tokenRepo.save(verificationToken);
        System.out.println("Verification token saved.");

        System.out.println("Sending verification email...");
        try {
            emailService.sendVerificationEmail(newUser, token);
            System.out.println("Verification email sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            e.printStackTrace();
            // Don't fail registration if email fails - user can still verify manually
            return ResponseEntity.ok(newUser.getUsername() + " you registered successfully. Note: There was an issue sending the verification email. Please contact support for manual verification.");
        }

        System.out.println("Returning 200 OK.");
        return ResponseEntity.ok(newUser.getUsername() + " you registered successfully. Please check your email for verification.");
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
        
        System.out.println("--- Fetched from userinfo endpoint: " + userInfo.toString());

        Optional<User> existingUser = userRepo.findByGmail(userInfo.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Check if user is verified and enabled
            if (!user.isVerified()) {
                return ResponseEntity.status(403).body("Please verify your email address before logging in. Check your email for the verification link.");
            }
            
            if (!user.isEnabled()) {
                return ResponseEntity.status(403).body("Your account is disabled. Please contact support.");
            }
            
            user.setPicture(userInfo.getPicture());
            userRepo.save(user);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getGmail());
            final String jwt = jwtUtil.generateToken(userDetails);
            final String userNickname = user.getUsername(); // Display name
            final String gmail = user.getGmail();           // Email for functionality
            final String role = userDetails.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_CUSTOMER");

            return ResponseEntity.ok(new LoginResponse(userNickname, gmail, role, jwt, user.getPicture(), user.getFullName(), user.getAddressLine1(), user.getAddressLine2(), user.getCity(), user.getPostalCode(), user.getTelephone()));

        } else {
           return ResponseEntity.status(404).body("User not found. Please register first.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("An error occurred during Google authentication.");
    }
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
            return ResponseEntity.status(400).body("Token expired");
        }

        user.setVerified(true);
        user.setEnabled(true);
        userRepo.save(user);

        return ResponseEntity.ok("User verified successfully");
}
}