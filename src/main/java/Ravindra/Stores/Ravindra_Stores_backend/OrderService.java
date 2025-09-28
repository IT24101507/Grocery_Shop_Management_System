package Ravindra.Stores.Ravindra_Stores_backend;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CheckoutRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Delivery fee configuration
    private static final BigDecimal HOME_DELIVERY_FEE = BigDecimal.valueOf(200.00); // Rs. 200 for home delivery
    private static final BigDecimal PICKUP_DELIVERY_FEE = BigDecimal.ZERO; // Free for pickup
    
    @Transactional
    public Order processCheckout(CheckoutRequest checkoutRequest, MultipartFile transferSlip) {
        try {
            // 1. Get user's cart
            Cart cart = cartRepository.findByUserIdWithItems(checkoutRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("Cart not found for user"));
            
            if (cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }
            
            // 2. Calculate totals
            BigDecimal subtotal = calculateSubtotal(cart);
            BigDecimal deliveryFee = calculateDeliveryFee(checkoutRequest.getDeliveryMethod());
            BigDecimal totalAmount = subtotal.add(deliveryFee);
            
            // 3. Validate stock availability
            validateStockAvailability(cart);
            
            // 4. Create order
            Order order = createOrder(checkoutRequest, subtotal, deliveryFee, totalAmount);
            
            // 5. Handle transfer slip if provided
            if (transferSlip != null && !transferSlip.isEmpty()) {
                try {
                    order.setTransferSlipFilename(transferSlip.getOriginalFilename());
                    order.setTransferSlipData(transferSlip.getBytes());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to process transfer slip: " + e.getMessage());
                }
            }
            
            // 6. Save order first to get ID
            order = orderRepository.save(order);
            
            // 7. Create order items from cart items
            createOrderItems(cart, order);
            
            // 8. Update product stock quantities
            updateProductStocks(cart);
            
            // 9. Clear user's cart
            clearCart(cart);
            
            // 10. Send confirmation email (async)
            try {
                sendOrderConfirmationEmail(order);
            } catch (Exception e) {
                System.err.println("Failed to send confirmation email: " + e.getMessage());
                // Don't fail the order if email fails
            }
            
            System.out.println("Order processed successfully: Order ID " + order.getOrderId());
            return order;
            
        } catch (Exception e) {
            System.err.println("Error processing checkout: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to process order: " + e.getMessage());
        }
    }
    
    private BigDecimal calculateSubtotal(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            BigDecimal itemPrice = product.getSalePrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        return subtotal;
    }
    
    private BigDecimal calculateDeliveryFee(String deliveryMethod) {
        if ("home".equals(deliveryMethod)) {
            return HOME_DELIVERY_FEE;
        }
        return PICKUP_DELIVERY_FEE;
    }
    
    private void validateStockAvailability(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getProName() + 
                                         ". Available: " + product.getStockQuantity() + 
                                         ", Requested: " + item.getQuantity());
            }
        }
    }
    
    private Order createOrder(CheckoutRequest request, BigDecimal subtotal, 
                             BigDecimal deliveryFee, BigDecimal totalAmount) {
        Order order = new Order(
            request.getUserId(),
            request.getCustomerName(),
            request.getMobileNumber(),
            request.getPaymentMethod(),
            request.getDeliveryMethod(),
            subtotal,
            totalAmount
        );
        
        order.setDeliveryFee(deliveryFee);
        
        // Set delivery address if home delivery
        if ("home".equals(request.getDeliveryMethod()) && request.getDeliveryAddress() != null) {
            order.setDeliveryStreet(request.getDeliveryAddress().getStreet());
            order.setDeliveryCity(request.getDeliveryAddress().getCity());
            order.setDeliveryPostalCode(request.getDeliveryAddress().getPostalCode());
        }
        
        return order;
    }
    
    private void createOrderItems(Cart cart, Order order) {
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal priceAtTime = product.getSalePrice();
            
            OrderItem orderItem = new OrderItem(order, product, cartItem.getQuantity(), priceAtTime);
            order.addOrderItem(orderItem);
        }
    }
    
    private void updateProductStocks(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int newStock = product.getStockQuantity() - item.getQuantity();
            product.setStockQuantity(newStock);
            productRepository.save(product);
        }
    }
    
    private void clearCart(Cart cart) {
        // Remove all cart items
        cartItemRepository.deleteAll(cart.getItems());
        // Remove cart
        cartRepository.delete(cart);
    }
    
    private void sendOrderConfirmationEmail(Order order) {
        try {
            // This is a placeholder - implement email sending logic
            String subject = "Order Confirmation - Order #" + order.getOrderId();
            String body = buildOrderConfirmationEmailBody(order);
            
            // Get user email from User entity (you'll need to implement this)
            // String userEmail = getUserEmailById(order.getUserId());
            // emailService.sendEmail(userEmail, subject, body);
            
            System.out.println("Order confirmation email prepared for Order #" + order.getOrderId());
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }
    
    private String buildOrderConfirmationEmailBody(Order order) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(order.getCustomerName()).append(",\n\n");
        body.append("Thank you for your order! Your order has been successfully placed.\n\n");
        body.append("Order Details:\n");
        body.append("Order ID: ").append(order.getOrderId()).append("\n");
        body.append("Order Date: ").append(order.getOrderDate()).append("\n");
        body.append("Total Amount: Rs. ").append(order.getTotalAmount()).append("\n");
        body.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
        body.append("Delivery Method: ").append(order.getDeliveryMethod()).append("\n");
        
        if ("home".equals(order.getDeliveryMethod())) {
            body.append("Delivery Address: ").append(order.getDeliveryStreet());
            if (order.getDeliveryCity() != null) {
                body.append(", ").append(order.getDeliveryCity());
            }
            if (order.getDeliveryPostalCode() != null) {
                body.append(" - ").append(order.getDeliveryPostalCode());
            }
            body.append("\n");
        }
        
        body.append("\nWe will contact you shortly to confirm your order.\n\n");
        body.append("Thank you for shopping with Ravindra Stores!\n");
        body.append("Contact: +94 XXX XXX XXX");
        
        return body.toString();
    }
    
    // Public methods for order management
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }
    
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findByOrderIdWithItems(orderId);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersNeedingVerification() {
        return orderRepository.findOrdersNeedingVerification();
    }
    
    public List<Order> getRecentOrders(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return orderRepository.findRecentOrders(threshold);
    }
    
    // Get order statistics
    public OrderStatistics getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Object[] stats = orderRepository.getOrderStatistics(startDate, endDate);
        if (stats != null && stats.length >= 3) {
            Long totalOrders = ((Number) stats[0]).longValue();
            BigDecimal totalRevenue = (BigDecimal) stats[1];
            BigDecimal averageOrderValue = (BigDecimal) stats[2];
            
            return new OrderStatistics(totalOrders, totalRevenue, averageOrderValue);
        }
        return new OrderStatistics(0L, BigDecimal.ZERO, BigDecimal.ZERO);
    }
    
    // Inner class for statistics
    public static class OrderStatistics {
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        
        public OrderStatistics(Long totalOrders, BigDecimal totalRevenue, BigDecimal averageOrderValue) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
        }
        
        // Getters
        public Long getTotalOrders() { return totalOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    }
}