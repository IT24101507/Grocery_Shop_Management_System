package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3002")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            Optional<User> userOptional = userRepository.findByGmail(userEmail);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOptional.get();
            
            UserProfileResponse profileResponse = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getGmail(),
                user.getFullName(),
                user.getTelephone(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getPostalCode(),
                user.getPicture()
            );

            return ResponseEntity.ok(profileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateRequest updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            Optional<User> userOptional = userRepository.findByGmail(userEmail);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(404).body("User not found");
            }

            User user = userOptional.get();
            
            
            if (updateRequest.getFullName() != null && !updateRequest.getFullName().trim().isEmpty()) {
                user.setFullName(updateRequest.getFullName().trim());
            }
            
            if (updateRequest.getTelephone() != null && !updateRequest.getTelephone().trim().isEmpty()) {
                user.setTelephone(updateRequest.getTelephone().trim());
            }
            
            if (updateRequest.getAddressLine1() != null) {
                user.setAddressLine1(updateRequest.getAddressLine1().trim());
            }
            
            if (updateRequest.getAddressLine2() != null) {
                user.setAddressLine2(updateRequest.getAddressLine2().trim());
            }
            
            if (updateRequest.getCity() != null && !updateRequest.getCity().trim().isEmpty()) {
                user.setCity(updateRequest.getCity().trim());
            }
            
            if (updateRequest.getPostalCode() != null && !updateRequest.getPostalCode().trim().isEmpty()) {
                user.setPostalCode(updateRequest.getPostalCode().trim());
            }

            
            userRepository.save(user);

        
            UserProfileResponse profileResponse = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getGmail(),
                user.getFullName(),
                user.getTelephone(),
                user.getAddressLine1(),
                user.getAddressLine2(),
                user.getCity(),
                user.getPostalCode(),
                user.getPicture()
            );

            return ResponseEntity.ok(profileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating profile: " + e.getMessage());
        }
    }
}


class UserProfileUpdateRequest {
    private String fullName;
    private String telephone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;

    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}


class UserProfileResponse {
    private Long id;
    private String username;
    private String gmail;
    private String fullName;
    private String telephone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String picture;

    public UserProfileResponse(Long id, String username, String gmail, String fullName, 
                             String telephone, String addressLine1, String addressLine2, 
                             String city, String postalCode, String picture) {
        this.id = id;
        this.username = username;
        this.gmail = gmail;
        this.fullName = fullName;
        this.telephone = telephone;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.picture = picture;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }
}
