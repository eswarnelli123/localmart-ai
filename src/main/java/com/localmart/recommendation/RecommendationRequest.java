package com.localmart.recommendation;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationRequest {
    // Optional user location for nearby sorting
    private Double userLat;
    private Double userLng;
    // Optional max distance in km
    private Double maxDistanceKm;

    // Optional shopping list items (product ids or names)
    private List<String> shoppingListItems;

    // Optional preferred categories
    private List<Long> categoryIds;

    // Number of results desired
    private Integer limit = 10;
}
