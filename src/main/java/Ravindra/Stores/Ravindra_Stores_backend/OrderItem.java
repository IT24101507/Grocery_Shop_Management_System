package Ravindra.Stores.Ravindra_Stores_backend;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;
    
    // Foreign key to Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // Foreign key to Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @NotNull
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName; // Store product name at time of order
    
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @NotNull
    @Column(name = "price_at_time", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtTime; // Store the price at the time of order
    
    @Column(name = "discount_at_time", columnDefinition = "INT DEFAULT 0")
    private Integer discountAtTime = 0; // Store discount percentage at time of order
    
    @NotNull
    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal; // quantity * priceAtTime
    
    @Column(name = "display_quantity")
    private Integer displayQuantity; // How much per unit (e.g., 500g, 1L)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "display_unit", length = 15)
    private UnitType displayUnit; // Unit type (KG, G, ML, L, etc.)
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal priceAtTime) {
        this.order = order;
        this.product = product;
        this.productName = product.getProName(); // Store name at time of order
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
        this.discountAtTime = product.getDiscount();
        this.displayQuantity = product.getDisplayQuantity();
        this.displayUnit = product.getDisplayUnit();
        this.lineTotal = priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public Long getOrderItemId() {
        return orderItemId;
    }
    
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        // Recalculate line total when quantity changes
        if (this.priceAtTime != null) {
            this.lineTotal = this.priceAtTime.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }
    
    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
        // Recalculate line total when price changes
        if (this.quantity != null) {
            this.lineTotal = priceAtTime.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    
    public Integer getDiscountAtTime() {
        return discountAtTime;
    }
    
    public void setDiscountAtTime(Integer discountAtTime) {
        this.discountAtTime = discountAtTime;
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
    
    public Integer getDisplayQuantity() {
        return displayQuantity;
    }
    
    public void setDisplayQuantity(Integer displayQuantity) {
        this.displayQuantity = displayQuantity;
    }
    
    public UnitType getDisplayUnit() {
        return displayUnit;
    }
    
    public void setDisplayUnit(UnitType displayUnit) {
        this.displayUnit = displayUnit;
    }
    
    // Helper methods
    public void recalculateLineTotal() {
        if (this.priceAtTime != null && this.quantity != null) {
            this.lineTotal = this.priceAtTime.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    
    // Get formatted display quantity with unit (e.g., "500g", "1L")
    public String getFormattedDisplayQuantity() {
        if (displayQuantity != null && displayUnit != null) {
            return displayQuantity + displayUnit.toString().toLowerCase();
        }
        return null;
    }
}