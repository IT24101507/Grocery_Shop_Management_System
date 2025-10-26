package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Submit review
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, String> requestBody) {
        try {
            Long orderId = Long.parseLong(requestBody.get("orderId"));
            Long productId = Long.parseLong(requestBody.get("productId"));
            Long customerId = Long.parseLong(requestBody.get("customerId"));
            int rating = Integer.parseInt(requestBody.get("rating"));
            String comment = requestBody.get("comment");

            Review review = reviewService.submitReview(orderId, productId, customerId, rating, comment);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all reviews (admin)
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // Get reviews by product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // Get reviews by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Review>> getReviewsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(reviewService.getReviewsByCustomer(customerId));
    }

    // Get review by id
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        if (review == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Review not found with id " + reviewId));
        }
        return ResponseEntity.ok(review);
    }

    // Update review (customer)
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody Map<String, String> requestBody) {
        try {
            Long customerId = Long.parseLong(requestBody.get("customerId"));
            int rating = Integer.parseInt(requestBody.get("rating"));
            String comment = requestBody.get("comment");

            Review updatedReview = reviewService.updateReview(reviewId, customerId, rating, comment);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete review (customer)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, @RequestParam Long customerId) {
        try {
            reviewService.deleteReview(reviewId, customerId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Delete review (admin)
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<?> deleteReviewAsAdmin(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReviewAsAdmin(reviewId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Admin reply
    @PutMapping("/admin/reply/{reviewId}")
    public ResponseEntity<?> replyToReview(@PathVariable Long reviewId, @RequestBody Map<String, String> requestBody) {
        try {
            String reply = requestBody.get("reply");
            Review updatedReview = reviewService.replyToReview(reviewId, reply);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
