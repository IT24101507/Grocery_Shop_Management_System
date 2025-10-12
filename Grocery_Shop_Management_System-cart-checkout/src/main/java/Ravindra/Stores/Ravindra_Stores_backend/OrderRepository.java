package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByTransferSlipPathIsNotNull();
}
