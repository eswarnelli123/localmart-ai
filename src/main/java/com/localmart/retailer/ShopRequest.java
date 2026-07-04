package com.localmart.retailer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopRequest {
    private String name;
    private String description;

    @NotBlank(message = "Location is required")
    private String location;              // address_line1

    private String addressLine2;
    private String city;
    private String state;
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    private String phone;
}
