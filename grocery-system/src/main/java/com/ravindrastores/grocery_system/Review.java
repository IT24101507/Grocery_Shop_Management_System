package Ravindra.Stores.Ravindra_Stores_backend;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long customerId;
    private String customerName;
    private Long orderId;

    @Column(nullable = false)
    private int rating; // 1-5 stars

    @Column(length = 1000)
    private String comment;

    private LocalDateTime reviewDate;

    @Column(name = "admin_reply")
    private String adminReply;

    @PrePersist
    protected void onCreate() {
        this.reviewDate = LocalDateTime.now();
    }
}
