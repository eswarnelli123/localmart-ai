package com.localmart.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.localmart.shop.Shop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnoreProperties({"products", "owner"})
    private Shop shop;

    @Column(name = "brand")
    private String brand;

    @NotBlank
    private String name;

    private String description;

    @Column(name = "base_price")
    private Double price;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "category_id", nullable = false)
    private Long categoryId = 1L;

    @Column(name = "taxable")
    private Boolean taxable = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    private Boolean active = true;

    @Transient
    private Integer stock;

    @Transient
    private Double priceOverride;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ProductImage> images = new ArrayList<>();

    @Transient
    @JsonProperty("effectivePrice")
    public Double getEffectivePrice() {
        return priceOverride != null ? priceOverride : price;
    }

    @Transient
    private String imagePath;

    @Transient
    @JsonProperty("imagePath")
    public String getImagePath() {
        String resolvedPath = imagePath;
        if (resolvedPath == null || resolvedPath.isBlank()) {
            if (images == null || images.isEmpty()) {
                return null;
            }
            resolvedPath = images.stream()
                    .filter(image -> image != null && image.getImageUrl() != null && !image.getImageUrl().isBlank())
                    .sorted((a, b) -> Integer.compare(a.getDisplayOrder() != null ? a.getDisplayOrder() : 1,
                            b.getDisplayOrder() != null ? b.getDisplayOrder() : 1))
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null);
        }
        if (resolvedPath == null || resolvedPath.isBlank()) {
            return null;
        }
        // Preserve external URLs (http://, https://, data: URIs)
        if (resolvedPath.startsWith("http://") || resolvedPath.startsWith("https://") || resolvedPath.startsWith("data:")) {
            return resolvedPath;
        }
        // Normalize relative paths to /uploads/... format
        String normalized = resolvedPath.replace("\\", "/");
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (!normalized.startsWith("/uploads/")) {
            normalized = "/uploads" + normalized;
        }
        return normalized;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

