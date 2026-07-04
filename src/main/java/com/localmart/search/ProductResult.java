package com.localmart.search;

import lombok.Data;

@Data
public class ProductResult {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long shopId;
    private String shopName;
    private Double distanceKm;
}
