package com.localmart.inventory;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "available_quantity")
    private Integer availableQuantity = 0;

    @Column(name = "reorder_threshold")
    private Integer reorderThreshold = 0;

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    @Column(name = "price_override")
    private Double priceOverride;
}
