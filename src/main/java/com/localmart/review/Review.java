package com.localmart.review;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "rating_id")
    private Long ratingId;

    @Column(name = "title")
    private String title;

    @Column(name = "review_text", columnDefinition = "TEXT", nullable = false)
    private String reviewText;

    @Column(name = "review_date")
    private LocalDateTime reviewDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.pending;

    public enum Status {
        published,
        pending,
        hidden
    }
}
