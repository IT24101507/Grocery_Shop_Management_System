package Ravindra.Stores.Ravindra_Stores_backend;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CheckoutRequest;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CartService cartService;

    // Create order from cart
    @Transactional
    public Order createOrderFromCart(Long customerId) {
        Optional<Cart> optionalCart = cartRepo.findByUserIdWithItems(customerId);

        if (optionalCart.isEmpty() || optionalCart.get().getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for customer " + customerId);
        }

        Cart cart = optionalCart.get();
        List<CartItem> cartItems = cart.getItems();

        // Stock check before creating order
        for (CartItem cartItem : cartItems) {
            Product product = productRepo.findById(cartItem.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + product.getName());
            }
        }

        // Create order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setDeliveryMethod("Default"); 
        order.setPaymentMethod("Default");  
        order.setStatus(OrderStatus.NEW);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal itemPrice = cartItem.getPriceAtTime().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(itemPrice);
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Save order and cascade save items
        Order savedOrder = orderRepo.save(order);

        // Clear cart items after saving order
        cartService.clearCart(customerId);

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

    public List<Order> getOrdersWithTransferSlips() {
        return orderRepo.findByTransferSlipPathIsNotNull();
    }

    // Save order (used for fixing transfer slip paths)
    public Order saveOrder(Order order) {
        return orderRepo.save(order);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with id " + orderId);
        }
        order.setStatus(status);

        if (status == OrderStatus.CONFIRMED) {
            order.setRevenue(order.getTotalPrice()); // Set revenue
            for (OrderItem item : order.getItems()) {
                Product product = productRepo.findById(item.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
                int newStock = product.getStockQuantity() - item.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("Not enough stock for product " + product.getName());
                }
                product.setStockQuantity(newStock);
                productRepo.save(product);
            }
        }

        return orderRepo.save(order);
    }

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public Order placeOrder(CheckoutRequest checkoutRequest, MultipartFile transferSlip) {
        Optional<Cart> optionalCart = cartRepo.findByUserIdWithItems(checkoutRequest.getUserId());

        if (optionalCart.isEmpty() || optionalCart.get().getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for customer " + checkoutRequest.getUserId());
        }

        Cart cart = optionalCart.get();
        List<CartItem> cartItems = cart.getItems();

        // Stock check before creating order
        for (CartItem cartItem : cartItems) {
            Product product = productRepo.findById(cartItem.getProduct().getId()).orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for product " + product.getName());
            }
        }

        // Create order
        Order order = new Order();
        order.setCustomerId(checkoutRequest.getUserId());
        order.setCustomerName(checkoutRequest.getCustomerName());
        order.setMobileNumber(checkoutRequest.getMobileNumber());
        order.setDeliveryMethod(checkoutRequest.getDeliveryMethod());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        if ("home".equals(checkoutRequest.getDeliveryMethod())) {
            CheckoutRequest.AddressDto address = checkoutRequest.getDeliveryAddress();
            if (address != null) {
                order.setStreet(address.getStreet());
                order.setCity(address.getCity());
                order.setPostalCode(address.getPostalCode());
            }
        }

        if (transferSlip != null) {
            String transferSlipPath = fileStorageService.save(transferSlip);
            order.setTransferSlipPath(transferSlipPath);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            BigDecimal itemPrice = cartItem.getPriceAtTime().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setPrice(itemPrice);
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(itemPrice);
        }

        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);

        // Save order and cascade save items
        Order savedOrder = orderRepo.save(order);

        // Clear cart items after saving order
        cartService.clearCart(checkoutRequest.getUserId());

        return savedOrder;
    }
}