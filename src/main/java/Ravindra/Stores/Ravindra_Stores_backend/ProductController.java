package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3002")
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    // Utility method for validating units
    private UnitType validateUnit(String unit, String type) {
        try {
            return UnitType.valueOf(unit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid " + type + ". Allowed values: KG, G, ML, L, PACKET, BOTTLE, CAN,OTHER");
        }
    }

    // Create a product
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestParam String proName,
            @RequestParam String category,
            @RequestParam double price,
            @RequestParam(defaultValue = "0") int discount,

            @RequestParam int stockQuantity,
            @RequestParam String stockUnit,

            @RequestParam int displayQuantity,
            @RequestParam String displayUnit,

            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        try {
            Product product = new Product();
            product.setProName(proName);
            product.setCategory(category);
            product.setPrice(price);
            product.setDiscount(discount);

            // Validate and set units
            product.setStockUnit(validateUnit(stockUnit, "stockUnit"));
            product.setStockQuantity(stockQuantity);
            product.setDisplayUnit(validateUnit(displayUnit, "displayUnit"));
            product.setDisplayQuantity(displayQuantity);

            product.setDescription(description);

            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImageData(imageFile.getBytes());
            }

            Product savedProduct = productRepo.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing image upload");
        }
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepo.findAll());
    }

    // Get a single product
   @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
    return productRepo.findById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found"));
   }

    // Serve product image
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        Optional<Product> product = productRepo.findById(id);

        if (product.isPresent() && product.get().getImageData() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // Default
            return new ResponseEntity<>(product.get().getImageData(), headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ✅ Update a product
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam String proName,
            @RequestParam String category,
            @RequestParam double price,
            @RequestParam(defaultValue = "0") int discount,

            @RequestParam int stockQuantity,
            @RequestParam String stockUnit,

            @RequestParam int displayQuantity,
            @RequestParam String displayUnit,

            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        Optional<Product> existingProduct = productRepo.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        try {
            Product product = existingProduct.get();
            product.setProName(proName);
            product.setCategory(category);
            product.setPrice(price);
            product.setDiscount(discount);

            product.setStockUnit(validateUnit(stockUnit, "stockUnit"));
            product.setStockQuantity(stockQuantity);

            product.setDisplayUnit(validateUnit(displayUnit, "displayUnit"));
            product.setDisplayQuantity(displayQuantity);

            product.setDescription(description);

            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImageData(imageFile.getBytes());
            }

            Product updatedProduct = productRepo.save(product);
            return ResponseEntity.ok(updatedProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing image upload");
        }
    }

    // ✅ Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!productRepo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
