package Ravindra.Stores.Ravindra_Stores_backend.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long productId;
    private int quantity;

    // Manual getters and setters as fallback
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}