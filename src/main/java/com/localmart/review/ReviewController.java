package com.localmart.review;

import com.localmart.rating.Rating;
import com.localmart.rating.RatingRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductRepository;
import com.localmart.shop.Shop;
import com.localmart.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    @GetMapping("/product/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable Long productId,
                                            @RequestParam(defaultValue = "published") Review.Status status) {
        return reviewRepository.findByProductIdAndStatus(productId, status);
    }

    @GetMapping("/store/{storeId}")
    public List<Review> getReviewsByStore(@PathVariable Long storeId,
                                          @RequestParam(defaultValue = "published") Review.Status status) {
        // Find products for the given store, then fetch reviews for those products
        Shop shop = shopRepository.findById(storeId).orElse(null);
        if (shop == null) {
            return List.of();
        }
        List<Product> products = productRepository.findByShop(shop);
        List<Long> productIds = products.stream().map(Product::getId).toList();
        if (productIds.isEmpty()) {
            return List.of();
        }
        return reviewRepository.findByProductIdInAndStatus(productIds, status);
    }

    @GetMapping("/customer/{customerId}")
    public List<Review> getReviewsByCustomer(@PathVariable Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    @PostMapping
    public ResponseEntity<Review> submitReview(@RequestBody ReviewRequest request) {
        Review review = new Review();
        review.setCustomerId(request.customerId());
        review.setProductId(request.productId());
        review.setTitle(request.title());
        review.setReviewText(request.reviewText());
        review.setReviewDate(LocalDateTime.now());
        review.setStatus(Review.Status.pending);

        if (request.ratingValue() != null) {
            Rating rating = new Rating();
            rating.setCustomerId(request.customerId());
            rating.setProductId(request.productId());
            rating.setRatingValue(request.ratingValue());
            rating.setSource(Rating.Source.product);
            rating.setCreatedAt(LocalDateTime.now());
            rating = ratingRepository.save(rating);
            review.setRatingId(rating.getId());
        }

        return ResponseEntity.ok(reviewRepository.save(review));
    }

    @PostMapping("/{reviewId}/respond")
    public ResponseEntity<Review> respondToReview(@PathVariable Long reviewId, @RequestBody ReviewResponseRequest req) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        // Review response is currently not supported in the entity schema.
        return ResponseEntity.ok(review);
    }

    @PostMapping("/{reviewId}/moderate")
    public ResponseEntity<Review> moderateReview(@PathVariable Long reviewId, @RequestBody ReviewModerationRequest req) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        review.setStatus(req.status());
        return ResponseEntity.ok(reviewRepository.save(review));
    }
}

record ReviewRequest(Long customerId, Long productId, Long storeId, String title, String reviewText, Integer ratingValue) {
}

record ReviewResponseRequest(Long retailerId, String responseText) {
}

record ReviewModerationRequest(Long adminId, Review.Status status) {
}
