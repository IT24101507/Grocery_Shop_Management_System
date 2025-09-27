package Ravindra.Stores.Ravindra_Stores_backend;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pro_id")
    private Long pro_id;

    @NotNull(message = "Product name cannot be null")
    @Column(name = "pro_name", nullable = false)
    private String proName;   // product name

    @NotNull(message = "Category cannot be null")
    @Column(nullable = false)
    private String category;  // product category

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    @Column(nullable = false)
    private double price;     // price per display quantity

    @Min(value = 0, message = "Discount cannot be negative")
    @Column(nullable = false, columnDefinition = "int default 0")
    private int discount = 0;  // default = 0 (percentage or flat)

    // 🔹 Total stock (e.g., 50 packets, 20 bottles, 100 kg)
    @NotNull(message = "Total stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private int stockQuantity; // how many units received from supplier

    @NotNull(message = "Stock unit type must be provided")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UnitType stockUnit;  

    
    @NotNull(message = "Display quantity is required")
    @Min(value = 0, message = "Display quantity cannot be negative")
    @Column(nullable = false)
    private int displayQuantity; // how much per unit displayed

    @NotNull(message = "Display unit type must be provided")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private UnitType displayUnit;  

    @Lob
    private String description;   // product description 

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    // Constructors
    public Product() {}

    public Product(String proName, String category, double price, int discount,
                   int stockQuantity, UnitType stockUnit,
                   int displayQuantity, UnitType displayUnit,
                   String description, byte[] imageData) {
        this.proName = proName;
        this.category = category;
        this.price = price;
        this.discount = discount;
        this.stockQuantity = stockQuantity;
        this.stockUnit = stockUnit;
        this.displayQuantity = displayQuantity;
        this.displayUnit = displayUnit;
        this.description = description;
        this.imageData = imageData;
    }

    // Getters and Setters
    public Long getPro_id() {
        return pro_id;
    }
    public void setPro_id(Long pro_id) {
        this.pro_id = pro_id;
    }

    public String getProName() {
        return proName;
    }
    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public UnitType getStockUnit() {
        return stockUnit;
    }
    public void setStockUnit(UnitType stockUnit) {
        this.stockUnit = stockUnit;
    }

    public int getDisplayQuantity() {
        return displayQuantity;
    }
    public void setDisplayQuantity(int displayQuantity) {
        this.displayQuantity = displayQuantity;
    }

    public UnitType getDisplayUnit() {
        return displayUnit;
    }
    public void setDisplayUnit(UnitType displayUnit) {
        this.displayUnit = displayUnit;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageData() {
        return imageData;
    }
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}


enum UnitType {
    KG, G, ML, L,
    PACKET, BOTTLE, CAN
}