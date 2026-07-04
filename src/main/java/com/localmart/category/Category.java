package com.localmart.category;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "category")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "parent_category_id")
    private Long parentCategoryId;

    @Column(name = "description")
    private String description;
}
