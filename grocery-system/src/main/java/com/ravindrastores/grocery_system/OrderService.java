package com.ravindrastores.grocery_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    // Create a new order
    public Order createOrder(Order order) {
        order.setStatus("NEW");
        order.setOrderDate(LocalDateTime.now());

        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        return orderRepo.save(order);
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    // Get order by ID
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepo.findById(orderId);
        return order.orElse(null); // returns null if not found
    }

    // Get orders by customer ID
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepo.findByCustomerId(customerId);
    }
}

