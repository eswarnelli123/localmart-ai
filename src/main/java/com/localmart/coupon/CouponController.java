package com.localmart.coupon;

import com.localmart.notification.NotificationService;
import com.localmart.user.User;
import com.localmart.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Coupon> getActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(coupon -> coupon.isActive()
                        && !coupon.getValidFrom().isAfter(LocalDateTime.now())
                        && !coupon.getValidUntil().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        return couponRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody CreateCouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setCode(request.code());
        coupon.setDescription(request.description());
        coupon.setDiscountType(request.discountType() != null ? request.discountType() : Coupon.DiscountType.percentage);
        coupon.setDiscountValue(request.discountValue());
        coupon.setMinOrderValue(request.minOrderValue());
        coupon.setMaxDiscountValue(request.maxDiscountValue());
        coupon.setValidFrom(request.validFrom() != null ? request.validFrom() : LocalDateTime.now());
        coupon.setValidUntil(request.validUntil() != null ? request.validUntil() : LocalDateTime.now().plusDays(30));
        coupon.setUsageLimit(request.usageLimit());
        coupon.setPerCustomerLimit(request.perCustomerLimit());
        coupon.setActive(request.active() == null ? true : request.active());
        coupon.setCreatedAt(LocalDateTime.now());

        Coupon savedCoupon = couponRepository.save(coupon);
        notificationService.sendCouponCreatedNotification(savedCoupon);
        return ResponseEntity.ok(savedCoupon);
    }

    @PostMapping("/redeem")
    public ResponseEntity<CouponRedemption> redeemCoupon(@RequestBody RedeemCouponRequest request) {
        User customer = getCurrentCustomer()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to redeem a coupon"));

        if (request.code() == null || request.code().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code must be provided");
        }

        return couponRepository.findByCode(request.code())
                .map(coupon -> {
                    if (!coupon.isActive() || coupon.getValidFrom().isAfter(LocalDateTime.now()) || coupon.getValidUntil().isBefore(LocalDateTime.now())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not active or valid at this time");
                    }

                    Long customerId = customer.getId();
                    if (redemptionRepository.findByCouponIdAndCustomerId(coupon.getId(), customerId).isPresent()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already redeemed this coupon");
                    }

                    CouponRedemption redemption = new CouponRedemption();
                    redemption.setCouponId(coupon.getId());
                    redemption.setCustomerId(customerId);
                    redemption.setOrderId(request.orderId());
                    redemption.setDiscountAmount(request.discountAmount());
                    redemption.setRedeemedAt(LocalDateTime.now());
                    return ResponseEntity.ok(redemptionRepository.save(redemption));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
    }

    private Optional<User> getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        if ((username == null || username.isBlank()) && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails principal) {
            username = principal.getUsername();
        }
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(username);
    }
}

record RedeemCouponRequest(String code, Long orderId, Double discountAmount) {
}

record CreateCouponRequest(String code,
                           String description,
                           Coupon.DiscountType discountType,
                           Double discountValue,
                           Double minOrderValue,
                           Double maxDiscountValue,
                           LocalDateTime validFrom,
                           LocalDateTime validUntil,
                           Integer usageLimit,
                           Integer perCustomerLimit,
                           Boolean active) {
}
