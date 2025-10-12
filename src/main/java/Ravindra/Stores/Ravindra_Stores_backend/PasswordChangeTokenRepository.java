package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordChangeTokenRepository extends JpaRepository<PasswordChangeToken, Long> {
    PasswordChangeToken findByToken(String token);
    PasswordChangeToken findByUser(User user);
}
