package com.localmart.rating;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "retailer_id")
    private Long retailerId;

    @Column(name = "rating_value", nullable = false)
    private Integer ratingValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private Source source;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Source {
        product,
        retailer,
        order
    }
}
