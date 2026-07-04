package com.localmart.recommendation;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponse {
    private List<OfferResult> bestNearbyOffers;
    private List<SimplifiedProduct> cheapestProducts;
    private List<SimplifiedProduct> alternativeProducts;
    private List<SimplifiedProduct> bestShoppingList;
    // optional AI text explanation
    private String aiExplanation;
}
