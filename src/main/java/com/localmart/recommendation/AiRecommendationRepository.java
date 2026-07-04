package com.localmart.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {
    List<AiRecommendation> findByCustomerIdOrderByScoreDesc(Long customerId);
    List<AiRecommendation> findByProductId(Long productId);
}
