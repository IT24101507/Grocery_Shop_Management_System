package Ravindra.Stores.Ravindra_Stores_backend;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data // Lombok: auto-generates getters, setters, toString(), etc.
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal salePrice; // Can be null if not on sale
    private int stockQuantity;
    private int displayQuantity; // Added to match database schema
    private String category; // Added to match database schema

    // Manual getters and setters as fallback
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public int getDisplayQuantity() { return displayQuantity; }
    public void setDisplayQuantity(int displayQuantity) { this.displayQuantity = displayQuantity; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}