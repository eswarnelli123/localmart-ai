package com.localmart.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId);
    List<Rating> findByRetailerId(Long retailerId);
    List<Rating> findByCustomerId(Long customerId);
}
