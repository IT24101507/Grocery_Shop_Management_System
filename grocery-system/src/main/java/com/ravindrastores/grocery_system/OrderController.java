package com.ravindrastores.grocery_system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ✅ Create order from customer's cart
    @PostMapping("/from-cart")
    public ResponseEntity<?> createOrderFromCart(@RequestParam Long customerId) {
        try {
            Order order = orderService.createOrderFromCart(customerId);
            return ResponseEntity.ok(order);

        } catch (RuntimeException e) {
            // Cart empty or other runtime issues
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // ✅ Get single order by ID
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

    // ✅ Get orders by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomer(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "No orders found for customerId " + customerId));
        }
        return ResponseEntity.ok(orders);
    }
}



