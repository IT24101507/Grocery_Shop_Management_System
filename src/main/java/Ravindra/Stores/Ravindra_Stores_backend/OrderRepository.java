package Ravindra.Stores.Ravindra_Stores_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find all orders for a specific user
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by user and status
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    
    // Find orders within a date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    // Find order with items (to avoid lazy loading issues)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByOrderIdWithItems(@Param("orderId") Long orderId);
    
    // Find orders by payment method
    List<Order> findByPaymentMethod(String paymentMethod);
    
    // Find orders by delivery method
    List<Order> findByDeliveryMethod(String deliveryMethod);
    
    // Find orders by customer name (useful for admin search)
    List<Order> findByCustomerNameContainingIgnoreCaseOrderByOrderDateDesc(String customerName);
    
    // Find orders by mobile number
    List<Order> findByMobileNumber(String mobileNumber);
    
    // Get total order count for a user
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    Long countOrdersByUserId(@Param("userId") Long userId);
    
    // Get total orders count by status
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countOrdersByStatus(@Param("status") OrderStatus status);
    
    // Find recent orders (last 30 days)
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :dateThreshold ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("dateThreshold") LocalDateTime dateThreshold);
    
    // Get orders that need transfer slip verification (bank transfer with pending status)
    @Query("SELECT o FROM Order o WHERE o.paymentMethod = 'bank-transfer' AND o.status = 'PENDING' AND o.transferSlipData IS NOT NULL ORDER BY o.orderDate ASC")
    List<Order> findOrdersNeedingVerification();
    
    // Find orders by city (for delivery analytics)
    List<Order> findByDeliveryCity(String city);
    
    // Custom query to get order statistics
    @Query("SELECT " +
           "COUNT(o) as totalOrders, " +
           "SUM(o.totalAmount) as totalRevenue, " +
           "AVG(o.totalAmount) as averageOrderValue " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Object[] getOrderStatistics(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);
}