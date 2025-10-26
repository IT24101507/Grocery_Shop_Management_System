package Ravindra.Stores.Ravindra_Stores_backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CheckoutRequest;

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
                return ResponseEntity.badRequest().body(createErrorObjectResponse("User ID is required"));
            }
            if (checkoutRequest.getCustomerName() == null || checkoutRequest.getCustomerName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorObjectResponse("Customer name is required"));
            }
            if (checkoutRequest.getMobileNumber() == null || checkoutRequest.getMobileNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorObjectResponse("Mobile number is required"));
            }
            if (checkoutRequest.getPaymentMethod() == null || checkoutRequest.getPaymentMethod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorObjectResponse("Payment method is required"));
            }
            if (checkoutRequest.getDeliveryMethod() == null || checkoutRequest.getDeliveryMethod().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorObjectResponse("Delivery method is required"));
            }
            
            // Validate delivery address for home delivery
            if ("home".equals(checkoutRequest.getDeliveryMethod())) {
                CheckoutRequest.AddressDto address = checkoutRequest.getDeliveryAddress();
                if (address == null || 
                    address.getStreet() == null || address.getStreet().trim().isEmpty() ||
                    address.getCity() == null || address.getCity().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(createErrorObjectResponse("Street address and city are required for home delivery"));
                }
            }
            
            // Validate transfer slip for bank transfer payment
            if ("bank-transfer".equals(checkoutRequest.getPaymentMethod()) && transferSlip == null) {
                return ResponseEntity.badRequest().body(createErrorObjectResponse("Transfer slip is required for bank transfer payment"));
            }

            // Log the received data for debugging
            System.out.println("=== ORDER CHECKOUT REQUEST ===");
            System.out.println("User ID: " + checkoutRequest.getUserId());
            System.out.println("Customer Name: " + checkoutRequest.getCustomerName());
            System.out.println("Mobile Number: " + checkoutRequest.getMobileNumber());
            System.out.println("Payment Method: " + checkoutRequest.getPaymentMethod());
            System.out.println("Delivery Method: " + checkoutRequest.getDeliveryMethod());
            
            if (checkoutRequest.getDeliveryAddress() != null) {
                System.out.println("Delivery Address:");
                System.out.println("  Street: " + checkoutRequest.getDeliveryAddress().getStreet());
                System.out.println("  City: " + checkoutRequest.getDeliveryAddress().getCity());
                System.out.println("  Postal Code: " + checkoutRequest.getDeliveryAddress().getPostalCode());
            }
            
            if (transferSlip != null) {
                System.out.println("Transfer Slip: " + transferSlip.getOriginalFilename() + " (" + transferSlip.getSize() + " bytes)");
            }
            System.out.println("==============================");

            Order order = orderService.placeOrder(checkoutRequest, transferSlip);

            return ResponseEntity.ok(createSuccessResponse("Order placed successfully!", order.getId()));

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorObjectResponse("Failed to place order: " + e.getMessage()));
        }
    }
    
    // Utility endpoint to fix transfer slip paths for existing orders
    @PostMapping("/fix-transfer-slip-paths")
    public ResponseEntity<Map<String, String>> fixTransferSlipPaths() {
        try {
            List<Order> orders = orderService.getOrdersWithTransferSlips();
            int fixedCount = 0;
            
            for (Order order : orders) {
                String path = order.getTransferSlipPath();
                String originalPath = path;
                
                if (path != null) {
                    // Fix Windows path separators
                    if (path.contains("\\")) {
                        path = path.replace("\\", "/");
                    }
                    
                    // Remove any uploads/ prefix
                    if (path.startsWith("uploads/")) {
                        path = path.substring(8);
                    }
                    
                    // Update the order if the path was changed
                    if (!path.equals(originalPath)) {
                        order.setTransferSlipPath(path);
                        orderService.saveOrder(order);
                        fixedCount++;
                    }
                }
            }
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Fixed " + fixedCount + " orders with incorrect transfer slip paths");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to fix transfer slip paths: " + e.getMessage()));
        }
    }
    
    private Map<String, Object> createSuccessResponse(String message, Long orderId) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("orderId", orderId);
        return response;
    }
    
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
    
    private Map<String, Object> createErrorObjectResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        return response;
    }

    @PostMapping("/from-cart")
    public ResponseEntity<?> createOrderFromCart(@RequestParam Long customerId) {
        try {
            Order order = orderService.createOrderFromCart(customerId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // Get single order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Order not found with id " + orderId));
        }
        return ResponseEntity.ok(order);
    }

    // Get orders by customer ID
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #customerId == principal.id")
    public ResponseEntity<?> getOrdersByCustomer(@PathVariable Long customerId) {
        System.out.println("customerId: " + customerId);
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/with-transfer-slips")
    public ResponseEntity<List<Order>> getOrdersWithTransferSlips() {
        List<Order> orders = orderService.getOrdersWithTransferSlips();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}




