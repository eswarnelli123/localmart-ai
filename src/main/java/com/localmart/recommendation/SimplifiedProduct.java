package com.localmart.recommendation;

import lombok.Data;

@Data
public class SimplifiedProduct {
    private Long id;
    private String name;
    private Double price;
    private Long shopId;
    private String shopName;
    private Double distanceKm;
}
