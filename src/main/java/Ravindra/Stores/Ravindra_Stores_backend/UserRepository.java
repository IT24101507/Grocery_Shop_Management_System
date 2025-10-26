package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByGmailAndPassword(String gmail, String password);
    Optional<User> findByGmail(String gmail);
}
