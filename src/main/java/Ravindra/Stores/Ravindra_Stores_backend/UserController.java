package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3002")

public class UserController {
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User input) {
        Optional<User> user = userRepo.findByGmail(input.getGmail());
        
        if (user.isPresent() && passwordEncoder.matches(input.getPassword(), user.get().getPassword())) {
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.get().getUsername());
            response.put("role", user.get().getRole());

            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid Credentials");
        }
        
    }
    

        @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        Optional<User> existingUser = userRepo.findByGmail(newUser.getGmail());

        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body("Gmail Already Exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepo.save(newUser);
        return ResponseEntity.ok("User Registered Successfully");
    }
}
