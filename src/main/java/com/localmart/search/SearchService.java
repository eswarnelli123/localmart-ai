package com.localmart.search;

import com.localmart.offer.OfferRepository;
import com.localmart.product.Product;
import com.localmart.product.ProductRepository;
import com.localmart.shop.Shop;
import com.localmart.shop.ShopRepository;
import com.localmart.util.MapsLinkParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final OfferRepository offerRepository;

    public SearchResult search(SearchRequest req) {
        List<Product> products = new ArrayList<>();
        if (req.getQuery() != null && !req.getQuery().isBlank()) {
            List<Product> all = productRepository.findByActiveTrue();
            products = new ArrayList<>();
            for (Product product : all) {
                if (com.localmart.product.ProductSearchMatcher.matches(product, req.getQuery())) {
                    products.add(product);
                }
            }
        } else if (req.getCategoryId() != null) {
            products = productRepository.findByCategoryIdAndActiveTrue(req.getCategoryId());
        } else {
            products = productRepository.findByActiveTrue();
        }

        // price filter
        if (req.getMinPrice() != null || req.getMaxPrice() != null) {
            products = products.stream().filter(p -> {
                double price = p.getEffectivePrice() != null ? p.getEffectivePrice() : (p.getPrice() != null ? p.getPrice() : 0);
                if (req.getMinPrice() != null && price < req.getMinPrice()) return false;
                if (req.getMaxPrice() != null && price > req.getMaxPrice()) return false;
                return true;
            }).collect(Collectors.toList());
        }

        // If hasOffer is requested, only keep products that are part of active offers
        if (Boolean.TRUE.equals(req.getHasOffer())) {
            List<com.localmart.offer.Offer> offers = offerRepository.findByActiveTrue();
            List<Long> productIds = offers.stream().map(com.localmart.offer.Offer::getProductId).filter(id -> id != null).collect(Collectors.toList());
            products = products.stream().filter(p -> productIds.contains(p.getId())).collect(Collectors.toList());
        }

        // Build product results and associated shops
        List<ProductResult> productResults = new ArrayList<>();
        for (Product p : products) {
            ProductResult pr = new ProductResult();
            pr.setId(p.getId());
            pr.setName(p.getName());
            pr.setDescription(p.getDescription());
            pr.setPrice(p.getEffectivePrice());
            if (p.getShop() != null) {
                pr.setShopId(p.getShop().getId());
                pr.setShopName(p.getShop().getName());
                Double lat = p.getShop().getLatitude();
                Double lng = p.getShop().getLongitude();
                if ((lat == null || lng == null)) {
                    double[] coords = MapsLinkParser.parseLatLng(p.getShop().getAddressLine1());
                    if (coords != null) {
                        lat = coords[0];
                        lng = coords[1];
                    }
                }
                if (lat != null && lng != null && req.getUserLat() != null && req.getUserLng() != null) {
                    pr.setDistanceKm(haversine(req.getUserLat(), req.getUserLng(), lat, lng));
                }
            }
            productResults.add(pr);
        }

        // Shops list
        List<Shop> shops = shopRepository.findAll();
        List<StoreResult> storeResults = new ArrayList<>();
        for (Shop s : shops) {
            StoreResult sr = new StoreResult();
            sr.setId(s.getId());
            sr.setName(s.getName());
            sr.setCity(s.getCity());
            sr.setAddress(s.getAddressLine1() + (s.getAddressLine2() != null ? " " + s.getAddressLine2() : ""));
            Double lat = s.getLatitude();
            Double lng = s.getLongitude();
            if (lat == null || lng == null) {
                double[] coords = MapsLinkParser.parseLatLng(s.getAddressLine1());
                if (coords != null) {
                    lat = coords[0];
                    lng = coords[1];
                }
            }
            if (lat != null && lng != null) {
                sr.setLatitude(lat);
                sr.setLongitude(lng);
                if (req.getUserLat() != null && req.getUserLng() != null) {
                    sr.setDistanceKm(haversine(req.getUserLat(), req.getUserLng(), lat, lng));
                }
            }
            storeResults.add(sr);
        }

        // If maxDistanceKm specified, filter stores and products
        if (req.getMaxDistanceKm() != null) {
            double maxKm = req.getMaxDistanceKm();
            storeResults = storeResults.stream().filter(st -> st.getDistanceKm() != null && st.getDistanceKm() <= maxKm).collect(Collectors.toList());
            productResults = productResults.stream().filter(pr -> pr.getDistanceKm() != null && pr.getDistanceKm() <= maxKm).collect(Collectors.toList());
        }

        // Sort by distance if user location provided
        if (req.getUserLat() != null && req.getUserLng() != null) {
            storeResults.sort(Comparator.comparing(st -> st.getDistanceKm() == null ? Double.MAX_VALUE : st.getDistanceKm()));
            productResults.sort(Comparator.comparing(pr -> pr.getDistanceKm() == null ? Double.MAX_VALUE : pr.getDistanceKm()));
        }

        SearchResult result = new SearchResult();
        result.setStores(storeResults);
        result.setProducts(productResults);
        return result;
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
