package com.localmart.coupon;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_redemption")
@Data
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redemption_id")
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt = LocalDateTime.now();

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;
}
