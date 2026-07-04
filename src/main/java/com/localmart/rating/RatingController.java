package com.localmart.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingRepository ratingRepository;

    @GetMapping("/product/{productId}")
    public List<Rating> getRatingsByProduct(@PathVariable Long productId) {
        return ratingRepository.findByProductId(productId);
    }

    @GetMapping("/retailer/{retailerId}")
    public List<Rating> getRatingsByRetailer(@PathVariable Long retailerId) {
        return ratingRepository.findByRetailerId(retailerId);
    }

    @GetMapping("/customer/{customerId}")
    public List<Rating> getRatingsByCustomer(@PathVariable Long customerId) {
        return ratingRepository.findByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<Rating> submitRating(@RequestBody RatingRequest request) {
        Rating rating = new Rating();
        rating.setCustomerId(request.customerId());
        rating.setProductId(request.productId());
        rating.setRetailerId(request.retailerId());
        rating.setRatingValue(request.ratingValue());
        rating.setSource(request.source() != null ? request.source() : Rating.Source.product);
        rating.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(ratingRepository.save(rating));
    }
}

record RatingRequest(Long customerId,
                     Long productId,
                     Long retailerId,
                     Integer ratingValue,
                     Rating.Source source) {
}
