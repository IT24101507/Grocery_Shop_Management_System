package com.ravindrastores.grocery_system;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ravindrastores.grocery_system.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}

