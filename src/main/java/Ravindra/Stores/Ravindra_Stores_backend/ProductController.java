package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Create a product with image upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @RequestParam("proName") String proName,
            @RequestParam("category") String category,
            @RequestParam("price") double price,
            @RequestParam(value = "discount", required = false, defaultValue = "0") int discount,

            // Stock info
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("stockUnit") String stockUnit,

            // Display info
            @RequestParam("displayQuantity") int displayQuantity,
            @RequestParam("displayUnit") String displayUnit,

            @RequestParam(value = "description", required = false) String description,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        Product product = new Product();
        product.setProName(proName);
        product.setCategory(category);
        product.setPrice(price);
        product.setDiscount(discount);

        // Stock unit validation
        try {
            product.setStockUnit(UnitType.valueOf(stockUnit.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid stockUnit. Allowed values: KG, G, ML, L, PACKET, BOTTLE, CAN");
        }
        product.setStockQuantity(stockQuantity);

        // Display unit validation
        try {
            product.setDisplayUnit(UnitType.valueOf(displayUnit.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid displayUnit. Allowed values: KG, G, ML, L, PACKET, BOTTLE, CAN");
        }
        product.setDisplayQuantity(displayQuantity);

        product.setDescription(description);

        // Save actual image binary
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageData(imageFile.getBytes());
        }

        Product savedProduct = productRepo.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepo.findAll());
    }

    // Get a single product
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepo.findById(id);
        return product.isPresent() ?
                ResponseEntity.ok(product.get()) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    // Serve product image separately
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isPresent() && product.get().getImageData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // or IMAGE_PNG depending on uploads
                    .body(product.get().getImageData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Update a product
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("proName") String proName,
            @RequestParam("category") String category,
            @RequestParam("price") double price,
            @RequestParam(value = "discount", required = false, defaultValue = "0") int discount,

            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("stockUnit") String stockUnit,

            @RequestParam("displayQuantity") int displayQuantity,
            @RequestParam("displayUnit") String displayUnit,

            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Optional<Product> existingProduct = productRepo.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        Product product = existingProduct.get();
        product.setProName(proName);
        product.setCategory(category);
        product.setPrice(price);
        product.setDiscount(discount);

        try {
            product.setStockUnit(UnitType.valueOf(stockUnit.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid stockUnit. Allowed values: KG, G, ML, L, PACKET, BOTTLE, CAN");
        }
        product.setStockQuantity(stockQuantity);

        try {
            product.setDisplayUnit(UnitType.valueOf(displayUnit.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid displayUnit. Allowed values: KG, G, ML, L, PACKET, BOTTLE, CAN");
        }
        product.setDisplayQuantity(displayQuantity);

        product.setDescription(description);

        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageData(imageFile.getBytes());
        }

        Product updatedProduct = productRepo.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}