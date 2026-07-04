package com.localmart.recommendation;

import com.localmart.offer.Offer;
import com.localmart.offer.OfferRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductRepository;
import com.localmart.search.SearchRequest;
import com.localmart.search.SearchService;
import com.localmart.shop.Shop;
import com.localmart.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final SearchService searchService;
    private final AiClient aiClient;

    public RecommendationResponse recommend(RecommendationRequest req) {
        RecommendationResponse resp = new RecommendationResponse();

        // 1) Best nearby offers: find offers for shops near user
        List<com.localmart.search.StoreResult> nearbyStores = Collections.emptyList();
        if (req.getUserLat() != null && req.getUserLng() != null) {
            com.localmart.search.SearchRequest sreq = new com.localmart.search.SearchRequest();
            sreq.setUserLat(req.getUserLat());
            sreq.setUserLng(req.getUserLng());
            sreq.setMaxDistanceKm(req.getMaxDistanceKm());
            var sres = searchService.search(sreq);
            nearbyStores = sres.getStores();
        }

        List<Offer> offers = offerRepository.findByActiveTrue();
        List<OfferResult> offerResults = offers.stream().map(o -> {
            OfferResult or = new OfferResult();
            or.setId(o.getId());
            or.setTitle(o.getTitle());
            or.setDescription(o.getDescription());
            or.setDiscount(o.getDiscount());
            or.setDiscountType(o.getDiscountType() != null ? o.getDiscountType().name() : null);
            or.setProductId(o.getProductId());
            if (o.getStore() != null) {
                or.setStoreId(o.getStore().getId());
                or.setStoreName(o.getStore().getName());
                if (req.getUserLat() != null && req.getUserLng() != null && o.getStore().getLatitude() != null) {
                    double d = haversine(req.getUserLat(), req.getUserLng(), o.getStore().getLatitude(), o.getStore().getLongitude());
                    or.setDistanceKm(d);
                }
            }
            return or;
        }).collect(Collectors.toList());

        // Sort offers by distance if available
        if (req.getUserLat() != null && req.getUserLng() != null) {
            offerResults.sort(Comparator.comparing(o -> o.getDistanceKm() == null ? Double.MAX_VALUE : o.getDistanceKm()));
        }
        resp.setBestNearbyOffers(offerResults.stream().limit(req.getLimit() == null ? 10 : req.getLimit()).collect(Collectors.toList()));

        // 2) Cheapest products: across selected categories or all
        List<Product> products;
        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            products = new ArrayList<>();
            for (Long cid : req.getCategoryIds()) products.addAll(productRepository.findByCategoryIdAndActiveTrue(cid));
        } else {
            products = productRepository.findByActiveTrue();
        }
        List<SimplifiedProduct> cheapest = products.stream()
                .sorted(Comparator.comparing(p -> p.getEffectivePrice() == null ? Double.MAX_VALUE : p.getEffectivePrice()))
                .limit(req.getLimit() == null ? 10 : req.getLimit())
                .map(this::toSimple)
                .collect(Collectors.toList());
        resp.setCheapestProducts(cheapest);

        // 3) Alternative products: for each cheapest product, find other products in same category
        List<SimplifiedProduct> alternatives = new ArrayList<>();
        for (SimplifiedProduct sp : cheapest) {
            Product p = productRepository.findById(sp.getId()).orElse(null);
            if (p == null) continue;
            List<Product> sameCat = productRepository.findByCategoryIdAndActiveTrue(p.getCategoryId());
            sameCat.stream().filter(pp -> !pp.getId().equals(p.getId()))
                    .sorted(Comparator.comparing(pp -> pp.getEffectivePrice() == null ? Double.MAX_VALUE : pp.getEffectivePrice()))
                    .limit(3)
                    .map(this::toSimple)
                    .forEach(alternatives::add);
        }
        resp.setAlternativeProducts(alternatives.stream().limit(req.getLimit() == null ? 10 : req.getLimit()).collect(Collectors.toList()));

        // 4) Best shopping list: simple heuristic — pick cheapest per requested shoppingListItems categories or recommend combinations
        List<SimplifiedProduct> shoppingList = new ArrayList<>();
        if (req.getShoppingListItems() != null && !req.getShoppingListItems().isEmpty()) {
            for (String item : req.getShoppingListItems()) {
                // search products by name using ProductSearchMatcher
                List<Product> all = productRepository.findByActiveTrue();
                List<Product> found = all.stream()
                        .filter(p -> com.localmart.product.ProductSearchMatcher.matches(p, item))
                        .collect(Collectors.toList());
                if (!found.isEmpty()) shoppingList.add(toSimple(found.get(0)));
            }
        } else {
            // default: top cheapest products
            shoppingList = cheapest.stream().limit(5).collect(Collectors.toList());
        }
        resp.setBestShoppingList(shoppingList);

        // AI enrichment: build prompt and ask LLM for explanation/curation
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a shopping assistant. Given the following context, provide: 1) short ranking rationale for best nearby offers, 2) suggestions for cheapest products and alternatives, 3) an optimized shopping list combining offers and lowest price items. Context:\n");
        prompt.append("User location: ").append(req.getUserLat()).append(",").append(req.getUserLng()).append("\n");
        prompt.append("Top offers:\n");
        for (OfferResult o : resp.getBestNearbyOffers()) {
            prompt.append(String.format("- %s (store=%s, discount=%s)\n", o.getTitle(), o.getStoreName(), o.getDiscount()));
        }
        prompt.append("Cheapest products:\n");
        for (SimplifiedProduct s : resp.getCheapestProducts()) {
            prompt.append(String.format("- %s (price=%s)\n", s.getName(), s.getPrice()));
        }

        String aiText = aiClient.generateText(prompt.toString());
        resp.setAiExplanation(aiText);

        return resp;
    }

    private SimplifiedProduct toSimple(Product p) {
        SimplifiedProduct sp = new SimplifiedProduct();
        sp.setId(p.getId());
        sp.setName(p.getName());
        sp.setPrice(p.getEffectivePrice());
        if (p.getShop() != null) {
            sp.setShopId(p.getShop().getId());
            sp.setShopName(p.getShop().getName());
            sp.setDistanceKm(p.getShop().getLatitude() != null && p.getShop().getLongitude() != null ?
                    haversine(0,0, p.getShop().getLatitude(), p.getShop().getLongitude()) : null);
        }
        return sp;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
