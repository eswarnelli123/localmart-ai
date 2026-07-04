package com.localmart.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_image")
@Data
@JsonIgnoreProperties({"product"})
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "display_order")
    private Integer displayOrder = 1;
}
