package Ravindra.Stores.Ravindra_Stores_backend;
import Ravindra.Stores.Ravindra_Stores_backend.dto.AddToCartRequest;
import Ravindra.Stores.Ravindra_Stores_backend.dto.CartDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam Long userId, @RequestBody AddToCartRequest request) {
        try {
            cartService.addToCart(userId, request.getProductId(), request.getQuantity());
            // Returning a simple JSON object for a clearer response
            return ResponseEntity.ok("{\"message\": \"Item added successfully\"}");
        } catch (RuntimeException e) {
            // We can return the actual error message to the frontend for better debugging
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    public ResponseEntity<CartDto> getCart(@RequestParam Long userId) {
        CartDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCartItem(@RequestParam Long userId, @RequestParam Long productId, @RequestParam int quantity) {
        try {
            cartService.updateCartItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok("{\"message\": \"Cart item updated successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok("{\"message\": \"Item removed successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestParam Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok("{\"message\": \"Cart cleared successfully\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}