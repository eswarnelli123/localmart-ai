package com.localmart.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.localmart.shop.Shop;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "offer")
@Data
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offer_id")
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_type")
    private OfferType offerType = OfferType.store;

    @ManyToOne
    @JoinColumn(name = "store_id")
    @JsonIgnoreProperties({"products", "owner"})
    private Shop store;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "category_id")
    private Long categoryId;

    @Transient
    private String category;

    @Transient
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType = DiscountType.percentage;

    @Column(name = "discount_value")
    private Double discount;

    @Column(name = "min_purchase_amount")
    private Double minPurchaseAmount;

    @Column(name = "start_date")
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "end_date")
    private LocalDateTime endDate = LocalDateTime.now().plusDays(7);

    @Column(name = "is_active")
    private boolean active = true;

    @Transient
    private String location;

    @Transient
    private String shopName;

    public String getLocation() {
        if (location != null) {
            return location;
        }
        return store != null ? store.getLocation() : null;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShopName() {
        if (shopName != null) {
            return shopName;
        }
        return store != null ? store.getName() : null;
    }

    public enum OfferType {
        product,
        store,
        category,
        sitewide
    }

    public enum DiscountType {
        percentage,
        fixed_amount,
        buy_x_get_y
    }
}
