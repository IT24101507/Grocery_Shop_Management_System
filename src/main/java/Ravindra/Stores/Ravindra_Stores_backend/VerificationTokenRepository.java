package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
    
    VerificationToken findByUserAndTokenType(User user, VerificationToken.TokenType tokenType);
}
