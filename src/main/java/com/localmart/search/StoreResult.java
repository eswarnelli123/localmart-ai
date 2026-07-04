package com.localmart.search;

import lombok.Data;

@Data
public class StoreResult {
    private Long id;
    private String name;
    private String city;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
}
