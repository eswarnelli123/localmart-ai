package com.localmart.wishlist;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist")
@Data
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_id")
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "added_at")
    private LocalDateTime addedAt = LocalDateTime.now();
}
