package com.localmart.retailer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfferRequest {
    private String title;
    private String description;
    private String category;
    private Double discount;
    private String discountType = "percentage";
    private Long productId;
    private Double minPurchaseAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
}
