package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("salePrice"), maxPrice);
            }
            if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("salePrice"), minPrice);
            }
            return criteriaBuilder.between(root.get("salePrice"), minPrice, maxPrice);
        };
    }

    public static Specification<Product> discountBetween(Integer minDiscount, Integer maxDiscount) {
        return (root, query, criteriaBuilder) -> {
            if (minDiscount == null && maxDiscount == null) {
                return criteriaBuilder.conjunction();
            }
            if (minDiscount == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("discount"), maxDiscount);
            }
            if (maxDiscount == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("discount"), minDiscount);
            }
            return criteriaBuilder.between(root.get("discount"), minDiscount, maxDiscount);
        };
    }
}
