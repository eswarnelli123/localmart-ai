package com.localmart.retailer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String brand;
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be zero or greater")
    private Double price;

    private Double discountPercent;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be zero or greater")
    private Integer stock;

    private String category;
    private String imagePath;
    private boolean active = true;
}
