package Ravindra.Stores.Ravindra_Stores_backend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "price_at_time", precision = 19, scale = 2)
    private BigDecimal priceAtTime; // Store the price when item was added to cart

    // Manual getters and setters as fallback
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPriceAtTime() { return priceAtTime; }
    public void setPriceAtTime(BigDecimal priceAtTime) { this.priceAtTime = priceAtTime; }
}