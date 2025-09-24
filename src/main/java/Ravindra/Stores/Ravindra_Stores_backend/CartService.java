package Ravindra.Stores.Ravindra_Stores_backend;

import Ravindra.Stores.Ravindra_Stores_backend.dto.CartDto;
import Ravindra.Stores.Ravindra_Stores_backend.dto.CartItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(ProductRepository productRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public void addToCart(Long userId, Long productId, int quantity) {
        // 1. Validate the product exists
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // 2. Get or create the cart for the user
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setUserId(userId);
                return cartRepository.save(newCart);
            });

        // 3. Check if this product is already in the cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);
        
        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            // 4. Check for sufficient stock
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock for " + product.getName() + ". Available: " + product.getStockQuantity());
            }
            
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // 4. Check for sufficient stock
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("Not enough stock for " + product.getName() + ". Available: " + product.getStockQuantity());
            }
            
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            // Store the current price (sale price takes priority)
            BigDecimal priceAtTime = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
            cartItem.setPriceAtTime(priceAtTime);
            
            cartItemRepository.save(cartItem);
        }

        System.out.println("Updated Cart for user '" + userId + "' with product: " + product.getName());
    }

    @Transactional(readOnly = true)
    public CartDto getCart(Long userId) {
        // 1. Get the cart with items from the database
        Optional<Cart> cartOpt = cartRepository.findByUserIdWithItems(userId);
        
        if (cartOpt.isEmpty()) {
            // Return empty cart if user has no cart yet
            CartDto emptyCart = new CartDto();
            emptyCart.setItems(new ArrayList<>());
            emptyCart.setTotalPrice(BigDecimal.ZERO);
            return emptyCart;
        }
        
        Cart cart = cartOpt.get();
        List<CartItemDto> detailedItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        // 2. Convert cart items to DTOs
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            CartItemDto itemDto = new CartItemDto();
            itemDto.setProductId(product.getId());
            itemDto.setName(product.getName());
            itemDto.setImageUrl(product.getImageUrl());
            itemDto.setQuantity(cartItem.getQuantity());
            
            // Use the current product price (sale price takes priority)
            BigDecimal currentPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
            itemDto.setPriceEach(currentPrice);

            // Calculate line total
            BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            itemDto.setLineTotal(lineTotal);
            grandTotal = grandTotal.add(lineTotal);

            detailedItems.add(itemDto);
        }

        // 3. Assemble the final CartDto object
        CartDto cartDto = new CartDto();
        cartDto.setItems(detailedItems);
        cartDto.setTotalPrice(grandTotal);

        return cartDto;
    }

    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        cartItemRepository.deleteByCartAndProduct(cart, product);
        
        System.out.println("Removed product " + product.getName() + " from cart for user: " + userId);
    }

    public void updateCartItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or less
            removeFromCart(userId, productId);
            return;
        }
        
        // Check stock availability
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for " + product.getName() + ". Available: " + product.getStockQuantity());
        }
        
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        System.out.println("Updated quantity for product " + product.getName() + " to " + quantity + " for user: " + userId);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            cartRepository.delete(cart);
            System.out.println("Cleared cart for user: " + userId);
        }
    }
}
