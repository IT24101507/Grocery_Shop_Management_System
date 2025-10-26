package com.ravindrastores.grocery_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import Ravindra.Stores.Ravindra_Stores_backend.Cart;
import Ravindra.Stores.Ravindra_Stores_backend.CartItem;
import Ravindra.Stores.Ravindra_Stores_backend.CartRepository;
import Ravindra.Stores.Ravindra_Stores_backend.CartItemRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    // Create order from cart
    public Order createOrderFromCart(Long customerId) {
        Optional<Cart> optionalCart = cartRepo.findByUserIdWithItems(customerId);

        if (optionalCart.isEmpty() || optionalCart.get().getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for customer " + customerId);
        }

        Cart cart = optionalCart.get();
        List<CartItem> cartItems = cart.getItems();

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setDeliveryMethod("Default"); // Optional: you can get this from cart if available
        order.setPaymentMethod("Default");  // Optional: same
        order.setStatus("NEW");
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPriceAtTime().doubleValue() * cartItem.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalPrice += orderItem.getPrice();
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Save order and cascade save items
        Order savedOrder = orderRepo.save(order);

        // Clear cart items after saving order
        cartItemRepo.deleteAll(cartItems);

        return savedOrder;
    }

    // Fetch all orders
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // Fetch order by ID
    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    // Fetch orders by customer
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }
}
