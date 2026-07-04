package com.localmart.recommendation;

import lombok.Data;

@Data
public class OfferResult {
    private Long id;
    private String title;
    private String description;
    private Double discount;
    private String discountType;
    private Long productId;
    private Long storeId;
    private String storeName;
    private Double distanceKm;
}
