package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category
    List<Product> findByCategory(String category);

    // Find products by name (case-insensitive search)
    List<Product> findByProNameContainingIgnoreCase(String proName);

    // Find products cheaper than or equal to a given price
    List<Product> findByPriceLessThanEqual(double price);

    // Find products more expensive than or equal to a given price
    List<Product> findByPriceGreaterThanEqual(double price);

    // Find products in a price range
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    // Find all products that have quantity greater than the given value
    List<Product> findByDisplayQuantityGreaterThan(int mindisplayQuantity);

    // Find all products that have quantity less than the given value
    List<Product> findByDisplayQuantityLessThan(int maxdisplayQuantity);
}