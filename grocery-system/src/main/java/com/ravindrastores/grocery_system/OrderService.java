package com.ravindrastores.grocery_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepo; // temporary mock cart

    // Create new order from cart
    public Order createOrderFromCart(Long customerId) {
        List<CartItem> cartItems = cartRepo.findByCustomerId(customerId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty for customer " + customerId);
        }

        CartItem firstItem = cartItems.get(0); // assume same customer info for all rows

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setDeliveryMethod(firstItem.getDeliveryMethod());
        order.setPaymentMethod(firstItem.getPaymentMethod());
        order.setStatus("NEW");
        order.setOrderDate(LocalDateTime.now());

        double totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        // ✅ Use regular for-loop instead of lambda
        for (CartItem cartItem : cartItems) {
            OrderItem item = new OrderItem();
            item.setProductId(cartItem.getProductId());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getTotalPrice()); // using mock column
            item.setOrder(order);

            orderItems.add(item);

            totalPrice += cartItem.getTotalPrice(); // works fine in loop
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Save order
        Order savedOrder = orderRepo.save(order);

        // Clear mock cart (optional for testing)
        cartRepo.deleteAll(cartItems);

        return savedOrder;
    }

    // Other methods
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }
}


