package Ravindra.Stores.Ravindra_Stores_backend;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CheckoutRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> placeOrder(
            @RequestParam("orderData") String orderDataJson,
            @RequestParam(value = "transferSlip", required = false) MultipartFile transferSlip) {
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CheckoutRequest checkoutRequest = objectMapper.readValue(orderDataJson, CheckoutRequest.class);

            // Basic validation
            if (checkoutRequest.getUserId() == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("User ID is required"));
            }
            if (checkoutRequest.getCustomerName() == null || checkoutRequest.getCustomerName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Customer name is required"));
            }
            if (checkoutRequest.getMobileNumber() == null || checkoutRequest.getMobileNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Mobile number is required"));
            }
            if (checkoutRequest.getPaymentMethod() == null || checkoutRequest.getPaymentMethod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Payment method is required"));
            }
            if (checkoutRequest.getDeliveryMethod() == null || checkoutRequest.getDeliveryMethod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Delivery method is required"));
            }
            
            // Validate delivery address for home delivery
            if ("home".equals(checkoutRequest.getDeliveryMethod())) {
                CheckoutRequest.AddressDto address = checkoutRequest.getDeliveryAddress();
                if (address == null || 
                    address.getStreet() == null || address.getStreet().trim().isEmpty() ||
                    address.getCity() == null || address.getCity().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Street address and city are required for home delivery"));
                }
            }
            
            // Validate transfer slip for bank transfer payment
            if ("bank-transfer".equals(checkoutRequest.getPaymentMethod()) && transferSlip == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Transfer slip is required for bank transfer payment"));
            }

            // Process the order using OrderService
            Order order = orderService.processCheckout(checkoutRequest, transferSlip);
            
            // Return success response with order details
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Order placed successfully!");
            successResponse.put("orderId", order.getOrderId());
            successResponse.put("orderDate", order.getOrderDate().toString());
            successResponse.put("totalAmount", order.getTotalAmount());
            successResponse.put("status", order.getStatus().toString());
            
            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to place order: " + e.getMessage()));
        }
    }
    
    // Additional endpoints for order management
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        try {
            Optional<Order> order = orderService.getOrderById(orderId);
            return order.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, 
                                                  @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}