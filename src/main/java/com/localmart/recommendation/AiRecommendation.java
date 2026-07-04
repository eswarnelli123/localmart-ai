package com.localmart.recommendation;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendation")
@Data
public class AiRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source = Source.personalized;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Source {
        collaborative_filtering,
        content_based,
        trending,
        personalized
    }
}
