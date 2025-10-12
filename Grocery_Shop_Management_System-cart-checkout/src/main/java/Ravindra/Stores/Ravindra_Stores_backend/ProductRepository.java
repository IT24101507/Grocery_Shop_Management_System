package Ravindra.Stores.Ravindra_Stores_backend;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Find products by category
    List<Product> findByCategory(String category);

    // Find products by name (case-insensitive search)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products cheaper than or equal to a given price
    List<Product> findBySalePriceLessThanEqual(BigDecimal price);

    // Find products more expensive than or equal to a given price
    List<Product> findBySalePriceGreaterThanEqual(BigDecimal price);

    // Find products in a price range
    List<Product> findBySalePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find all products that have quantity greater than the given value
    List<Product> findByDisplayQuantityGreaterThan(int mindisplayQuantity);

    // Find all products that have quantity less than the given value
    List<Product> findByDisplayQuantityLessThan(int maxdisplayQuantity);
}
