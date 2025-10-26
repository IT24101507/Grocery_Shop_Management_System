package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private OrderRepository orderRepo;

    // Get all reviews
    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    // Submit a review
    public Review submitReview(Long orderId, Long productId, Long customerId, int rating, String comment) {

        // 1. Check if order exists
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));

        // 2. Verify order belongs to customer
        if (!order.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Order does not belong to customer " + customerId);
        }

        // 3. Check if order is delivered
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Can only review products from delivered orders");
        }

        // 4. Check if product exists in this order
        boolean productInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));

        if (!productInOrder) {
            throw new RuntimeException("Product " + productId + " not found in order " + orderId);
        }

        // 5. Check if already reviewed
        Optional<Review> existingReview = reviewRepo.findByOrderIdAndProductIdAndCustomerId(orderId, productId, customerId);
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this product for this order");
        }

        // 6. Validate rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // 7. Save review
        Review review = new Review();
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setCustomerId(customerId);
        review.setCustomerName(order.getCustomerName());
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepo.save(review);
    }

    // Get reviews by product
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepo.findByProductId(productId);
    }

    // Get reviews by customer
    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepo.findByCustomerId(customerId);
    }

    // Get review by id
    public Review getReviewById(Long reviewId) {
        return reviewRepo.findById(reviewId).orElse(null);
    }

    // Update review (customer)
    public Review updateReview(Long reviewId, Long customerId, int rating, String comment) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + reviewId));

        if (!review.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Review does not belong to customer " + customerId);
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        review.setRating(rating);
        review.setComment(comment);

        return reviewRepo.save(review);
    }

    // Delete review (customer)
    public void deleteReview(Long reviewId, Long customerId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + reviewId));

        if (!review.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Review does not belong to customer " + customerId);
        }

        reviewRepo.delete(review);
    }

    // Delete review as admin
    public void deleteReviewAsAdmin(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + reviewId));
        reviewRepo.delete(review);
    }

    // Admin reply
    public Review replyToReview(Long reviewId, String reply) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + reviewId));

        if (reply == null || reply.isBlank()) {
            throw new RuntimeException("Reply cannot be empty");
        }

        review.setAdminReply(reply);
        return reviewRepo.save(review);
    }
}
