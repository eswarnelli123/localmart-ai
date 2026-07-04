package com.localmart.shop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localmart.util.MapsLinkParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopGeocodeService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    private final ShopRepository shopRepository;
    private final RestTemplate restTemplate;

    public ShopGeocodeService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public Shop geocodeShop(Shop shop) {
        if (shop == null) return null;

        double[] coords = parseCoordinatesFromShop(shop);
        if (coords != null) {
            shop.setLatitude(coords[0]);
            shop.setLongitude(coords[1]);
            return shopRepository.save(shop);
        }

        return shop;
    }

    @Transactional
    public Shop geocodeShopById(Long id) {
        return shopRepository.findById(id).map(this::geocodeShop).orElse(null);
    }

    private double[] parseCoordinatesFromShop(Shop shop) {
        double[] coords = MapsLinkParser.parseLatLng(shop.getAddressLine1());
        if (coords == null && shop.getAddressLine2() != null) {
            coords = MapsLinkParser.parseLatLng(shop.getAddressLine2());
        }
        if (coords != null) {
            return coords;
        }

        return lookupCoordinatesFromAddress(shop);
    }

    private double[] lookupCoordinatesFromAddress(Shop shop) {
        String query = buildAddressQuery(shop);
        if (query == null || query.isBlank()) {
            return null;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "LocalMartAI/1.0");
            headers.set("Accept", "application/json");

            String url = UriComponentsBuilder.fromHttpUrl(NOMINATIM_URL)
                    .queryParam("format", "jsonv2")
                    .queryParam("limit", 1)
                    .queryParam("q", query)
                    .build(true)
                    .toUriString();

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            if (root.isArray() && !root.isEmpty()) {
                JsonNode first = root.get(0);
                JsonNode lat = first.get("lat");
                JsonNode lon = first.get("lon");
                if (lat != null && lon != null) {
                    return new double[]{lat.asDouble(), lon.asDouble()};
                }
            }
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }

    String buildAddressQuery(Shop shop) {
        if (shop == null) {
            return null;
        }

        List<String> parts = new ArrayList<>();
        addPart(parts, shop.getAddressLine1());
        addPart(parts, shop.getAddressLine2());
        addPart(parts, shop.getCity());
        addPart(parts, shop.getState());
        addPart(parts, shop.getPostalCode());
        addPart(parts, shop.getCountry());

        return String.join(", ", parts);
    }

    private void addPart(List<String> parts, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(value.trim());
        }
    }
}
