package Ravindra.Stores.Ravindra_Stores_backend;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // Define the enum inside the Product class
    public enum UnitType {
        KG,
        G,
        ML,
        L,
        PACKET,
        BOTTLE,
        CAN,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pro_id")
    private Long id;

    @NotNull(message = "Product name cannot be null")
    @Column(name = "pro_name", nullable = false)
    private String name;

    @NotNull(message = "Category cannot be null")
    @Column(nullable = false)
    private String category;

    @NotNull(message = "Original price cannot be null")
    @Min(value = 0, message = "Original price cannot be negative")
    @Column(nullable = false)
    private BigDecimal originalPrice;

    @Min(value = 0, message = "Sale price cannot be negative")
    private BigDecimal salePrice;

    @Min(value = 0, message = "Discount cannot be negative")
    @Column(nullable = false, columnDefinition = "int default 0")
    private int discount = 0;

    @NotNull(message = "Total stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private int stockQuantity;

    @NotNull(message = "Stock unit type must be provided")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UnitType stockUnit; // The type is now Product.UnitType

    @NotNull(message = "Display quantity is required")
    @Min(value = 0, message = "Display quantity cannot be negative")
    @Column(nullable = false)
    private int displayQuantity;

    @NotNull(message = "Display unit type must be provided")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UnitType displayUnit; // The type is now Product.UnitType

    @Lob
    private String description;

    private String imageUrl;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[]imageData;

    @Column(name = "custom_stock_unit")
    private String customStockUnit;

    @Column(name = "custom_display_unit")
    private String customDisplayUnit;
}
