package com.localmart.search;

import lombok.Data;

@Data
public class SearchRequest {
    private String query; // product or store name
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Boolean hasOffer;
    private Double userLat;
    private Double userLng;
    private Double maxDistanceKm; // optional
}
