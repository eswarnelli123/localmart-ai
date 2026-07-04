package com.localmart.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shops")
@RequiredArgsConstructor
public class ShopAdminController {

    private final ShopRepository shopRepository;
    private final ShopGeocodeService geocodeService;

    @PostMapping("/{id}/geocode")
    public ResponseEntity<Shop> geocodeShop(@PathVariable Long id) {
        Shop s = geocodeService.geocodeShopById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @PostMapping("/geocode/batch")
    public ResponseEntity<Integer> geocodeBatch() {
        List<Shop> shops = shopRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull();
        int updated = 0;
        for (Shop s : shopRepository.findAll()) {
            Shop before = s;
            Shop after = geocodeService.geocodeShop(s);
            if (after != null && (after.getLatitude() != null && after.getLongitude() != null)
                    && (before.getLatitude() == null || before.getLongitude() == null)) {
                updated++;
            }
        }
        return ResponseEntity.ok(updated);
    }
}
