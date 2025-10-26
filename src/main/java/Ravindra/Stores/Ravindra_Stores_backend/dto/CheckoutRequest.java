package Ravindra.Stores.Ravindra_Stores_backend.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long userId; // Changed from String to Long to match frontend
    private String customerName;
    private String mobileNumber;
    private String paymentMethod; 
    private String deliveryMethod; 
    private AddressDto deliveryAddress;

    @Data
    public static class AddressDto {
        private String street;
        private String city;
        private String postalCode;
        
        // Manual getters and setters as fallback
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    }
    
    // Manual getters and setters as fallback
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    
    public AddressDto getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(AddressDto deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}