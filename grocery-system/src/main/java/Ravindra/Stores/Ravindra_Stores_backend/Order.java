package Ravindra.Stores.Ravindra_Stores_backend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String customerName;
    private String mobileNumber;
    private String deliveryMethod;
    private String paymentMethod;
    private BigDecimal totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal revenue; // Add revenue field

    private java.time.LocalDateTime orderDate;

    public java.time.LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(java.time.LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    private String transferSlipPath;

    private String street;
    private String city;
    private String postalCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

}
