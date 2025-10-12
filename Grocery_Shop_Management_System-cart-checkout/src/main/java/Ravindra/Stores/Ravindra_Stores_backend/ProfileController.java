package Ravindra.Stores.Ravindra_Stores_backend;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3002") // Enables cross-origin requests
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * CREATE a new product with an optional image upload.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam BigDecimal originalPrice,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(defaultValue = "0") int discount,
            @RequestParam int stockQuantity,
            @RequestParam String stockUnit,
            @RequestParam(required = false) String customStockUnit, // New field for custom stock unit
            @RequestParam int displayQuantity,
            @RequestParam String displayUnit,
            @RequestParam(required = false) String customDisplayUnit, // New field for custom display unit
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setCategory(category);
            product.setOriginalPrice(originalPrice);

            // Calculate sale price based on discount
            if (discount > 0) {
                BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discount).divide(BigDecimal.valueOf(100));
                BigDecimal calculatedSalePrice = originalPrice.multiply(discountMultiplier);
                product.setSalePrice(calculatedSalePrice);
            } else {
                product.setSalePrice(originalPrice);
            }

            product.setDiscount(discount);
            product.setStockQuantity(stockQuantity);
            product.setDisplayQuantity(displayQuantity);
            product.setDescription(description);
            product.setImageUrl(imageUrl);

            // Handle stock unit
            if (Product.UnitType.OTHER.name().equalsIgnoreCase(stockUnit)) {
                product.setStockUnit(Product.UnitType.OTHER);
                product.setCustomStockUnit(customStockUnit); // Set the custom unit
            } else {
                product.setStockUnit(Product.UnitType.valueOf(stockUnit.toUpperCase()));
            }

            // Handle display unit
            if (Product.UnitType.OTHER.name().equalsIgnoreCase(displayUnit)) {
                product.setDisplayUnit(Product.UnitType.OTHER);
                product.setCustomDisplayUnit(customDisplayUnit); // Set the custom unit
            } else {
                product.setDisplayUnit(Product.UnitType.valueOf(displayUnit.toUpperCase()));
            }

            // Handle image data from uploaded file
            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImageData(imageFile.getBytes());
            }

            Product savedProduct = productRepository.save(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Catches enum validation errors
            return ResponseEntity.badRequest().body("Invalid unit type provided.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image file: " + e.getMessage());
        }
    }

    /**
     * READ all products.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) Integer maxDiscount
    ) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(category))
                .and(ProductSpecification.priceBetween(minPrice, maxPrice))
                .and(ProductSpecification.discountBetween(minDiscount, maxDiscount));
        List<Product> products = productRepository.findAll(spec);
        return ResponseEntity.ok(products);
    }

    /**
     * READ a single product by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + id));
    }

    /**
     * READ the image data for a specific product.
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        Optional<Product> productOptional = productRepository.findById(id);

        if (productOptional.isPresent() && productOptional.get().getImageData() != null) {
            Product product = productOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(product.getImageData(), headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
        return ResponseEntity.ok(products);
    }

    /**
     * UPDATE an existing product by its ID.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam BigDecimal originalPrice,
            @RequestParam(required = false) BigDecimal salePrice,
            @RequestParam(defaultValue = "0") int discount,
            @RequestParam int stockQuantity,
            @RequestParam String stockUnit,
            @RequestParam(required = false) String customStockUnit, 
            @RequestParam int displayQuantity,
            @RequestParam String displayUnit,
            @RequestParam(required = false) String customDisplayUnit, 
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile imageFile
    ) {
        Optional<Product> existingProductOptional = productRepository.findById(id);
        if (existingProductOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + id);
        }

        try {
            Product productToUpdate = existingProductOptional.get();
            productToUpdate.setName(name);
            productToUpdate.setCategory(category);
            productToUpdate.setOriginalPrice(originalPrice);

            if (discount > 0) {
                BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discount).divide(BigDecimal.valueOf(100));
                BigDecimal calculatedSalePrice = originalPrice.multiply(discountMultiplier);
                productToUpdate.setSalePrice(calculatedSalePrice);
            } else {
                productToUpdate.setSalePrice(originalPrice);
            }

            productToUpdate.setDiscount(discount);
            productToUpdate.setStockQuantity(stockQuantity);
            productToUpdate.setDisplayQuantity(displayQuantity);
            productToUpdate.setDescription(description);
            productToUpdate.setImageUrl(imageUrl);

            // Handle stock unit
            if (Product.UnitType.OTHER.name().equalsIgnoreCase(stockUnit)) {
                productToUpdate.setStockUnit(Product.UnitType.OTHER);
                productToUpdate.setCustomStockUnit(customStockUnit);
            } else {
                productToUpdate.setStockUnit(Product.UnitType.valueOf(stockUnit.toUpperCase()));
                productToUpdate.setCustomStockUnit(null); // Clear custom unit if not "OTHER"
            }

            // Handle display unit
            if (Product.UnitType.OTHER.name().equalsIgnoreCase(displayUnit)) {
                productToUpdate.setDisplayUnit(Product.UnitType.OTHER);
                productToUpdate.setCustomDisplayUnit(customDisplayUnit);
            } else {
                productToUpdate.setDisplayUnit(Product.UnitType.valueOf(displayUnit.toUpperCase()));
                productToUpdate.setCustomDisplayUnit(null); // Clear custom unit if not "OTHER"
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                productToUpdate.setImageData(imageFile.getBytes());
            }

            Product updatedProduct = productRepository.save(productToUpdate);
            return ResponseEntity.ok(updatedProduct);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid unit type provided.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image file: " + e.getMessage());
        }
    }

    /**
     * DELETE a product by its ID.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product with id " + id + " was deleted successfully.");
    }
}
