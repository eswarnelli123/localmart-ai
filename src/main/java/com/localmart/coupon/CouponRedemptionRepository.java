package com.localmart.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    Optional<CouponRedemption> findByCouponIdAndCustomerId(Long couponId, Long customerId);
}
