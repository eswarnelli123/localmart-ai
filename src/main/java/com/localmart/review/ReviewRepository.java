package com.localmart.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndStatus(Long productId, Review.Status status);
    List<Review> findByCustomerId(Long customerId);
    List<Review> findByProductIdInAndStatus(List<Long> productIds, Review.Status status);
}
