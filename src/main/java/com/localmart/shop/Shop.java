package com.localmart.shop;

import com.localmart.retailer.Retailer;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
@Data
@JsonIgnoreProperties({"products", "owner"})
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @NotBlank
    @Column(name = "store_name")
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "store_description")
    private String description;

    @Column(name = "address_line1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private String location;

    @Transient
    private String logoPath;

    @Transient
    private String bannerPath;

    public String getLocation() {
        if (location != null) {
            return location;
        }
        return addressLine1;
    }

    public void setLocation(String location) {
        this.location = location;
        this.addressLine1 = location;
    }

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    private Retailer owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<com.localmart.product.Product> products = new ArrayList<>();
}
