package com.localmart.wishlist;

import com.localmart.user.User;
import com.localmart.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    @GetMapping
    public List<Wishlist> getWishlist() {
        User customer = getCurrentCustomer()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to view your wishlist"));
        return wishlistRepository.findByCustomerId(customer.getId());
    }

    @PostMapping
    public ResponseEntity<Wishlist> addToWishlist(@RequestBody WishlistRequest request) {
        User customer = getCurrentCustomer()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to add items to your wishlist"));

        if (request.productId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing product ID");
        }

        Long customerId = customer.getId();
        Long productId = request.productId();
        return wishlistRepository.findByCustomerIdAndProductId(customerId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Wishlist wishlist = new Wishlist();
                    wishlist.setCustomerId(customerId);
                    wishlist.setProductId(productId);
                    return ResponseEntity.ok(wishlistRepository.save(wishlist));
                });
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFromWishlist(@RequestParam Long productId) {
        User customer = getCurrentCustomer()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to update your wishlist"));

        Long customerId = customer.getId();
        return wishlistRepository.findByCustomerIdAndProductId(customerId, productId)
                .map(existing -> {
                    wishlistRepository.delete(existing);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }

    private Optional<User> getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        if ((username == null || username.isBlank()) && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails principal) {
            username = principal.getUsername();
        }
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(username);
    }
}

record WishlistRequest(Long productId) {
}
