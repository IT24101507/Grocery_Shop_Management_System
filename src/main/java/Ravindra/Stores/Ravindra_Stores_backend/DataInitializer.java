package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ProductRepository productRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            List<Product> products = Arrays.asList(
                createProduct(1L, "Fresh Carrots (500g)", "/images/carrots.jpg", new BigDecimal("600"), null, 100, 50, "Vegetables"),
                createProduct(2L, "Apple", "/images/red-apple.jpg", new BigDecimal("600"), new BigDecimal("500"), 50, 25, "Fruits"),
                createProduct(3L, "Pineapple", "/images/pineapple.jpg", new BigDecimal("400"), null, 75, 35, "Fruits"),
                createProduct(4L, "Pet Food", "/images/pet-food.jpg", new BigDecimal("550"), null, 40, 20, "Pet Supplies"),
                createProduct(5L, "Cheese", "/images/cheese.jpg", new BigDecimal("420"), null, 2, 1, "Dairy")
            );
            productRepository.saveAll(products);
            System.out.println("--- Mock products have been loaded into the database. ---");
        }

        if (userRepository.count() == 0) {
            List<User> users = Arrays.asList(
                createUser("John Doe", "john.doe@email.com", "password123", "ROLE_CUSTOMER", true, true, null),
                createUser("Jane Smith", "jane.smith@email.com", "password123", "ROLE_CUSTOMER", true, true, null),
                createUser("Admin User", "admin@grocery.com", "admin123", "ROLE_ADMIN", true, true, null),
                createUser("Mike Johnson", "mike.johnson@email.com", "password123", "ROLE_CUSTOMER", false, false, null), // Unverified user
                createUser("Sarah Wilson", "sarah.wilson@gmail.com", "googleuser", "ROLE_CUSTOMER", true, true, "https://lh3.googleusercontent.com/a/default-user=s96-c") // Google user
            );
            userRepository.saveAll(users);
            System.out.println("--- Mock users have been loaded into the database. ---");
        }
    }

    private Product createProduct(Long id, String name, String imageUrl, BigDecimal price, BigDecimal salePrice, int stockQuantity, int displayQuantity, String category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setImageUrl(imageUrl);
        product.setPrice(price);
        product.setSalePrice(salePrice);
        product.setStockQuantity(stockQuantity);
        product.setDisplayQuantity(displayQuantity);
        product.setCategory(category);
        return product;
    }

    private User createUser(String username, String gmail, String password, String role, boolean verified, boolean enabled, String picture) {
        User user = new User();
        user.setUsername(username);
        user.setGmail(gmail);
        user.setPassword(passwordEncoder.encode(password)); // Encode the password
        user.setRole(role);
        user.setVerified(verified);
        user.setEnabled(enabled);
        user.setPicture(picture);
        return user;
    }
}