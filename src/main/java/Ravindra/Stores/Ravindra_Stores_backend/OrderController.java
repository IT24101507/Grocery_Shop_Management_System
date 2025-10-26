package Ravindra.Stores.Ravindra_Stores_backend;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CheckoutRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping(value = "/place", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> placeOrder(
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

            // TODO:
            // 1. Validate the cart exists and has items
            // 2. Calculate total amount including delivery fee
            // 3. Save order to database
            // 4. Save transfer slip file if provided
            // 5. Clear the user's cart
            // 6. Send confirmation email
            // 7. Return order confirmation details

            return ResponseEntity.ok(createSuccessResponse("Order placed successfully!"));

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to place order: " + e.getMessage()));
        }
    }
    
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
    
    private Map<String, String> createErrorResponse(String error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        return response;
    }
}