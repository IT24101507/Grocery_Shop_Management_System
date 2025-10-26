package Ravindra.Stores.Ravindra_Stores_backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long productId;
    private String name;
    private String imageUrl;
    private int quantity;
    private BigDecimal priceEach; 
    private BigDecimal lineTotal;

    // Manual getters and setters as fallback
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceEach() { return priceEach; }
    public void setPriceEach(BigDecimal priceEach) { this.priceEach = priceEach; }
    
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}