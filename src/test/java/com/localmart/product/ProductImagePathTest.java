package com.localmart.product;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductImagePathTest {

    @Test
    void getImagePathShouldNormalizeRelativeUploadPaths() {
        Product product = new Product();
        product.setImagePath("uploads/test-product.png");

        assertThat(product.getImagePath()).isEqualTo("/uploads/test-product.png");
    }

    @Test
    void getImagePathShouldPreserveExternalImageUrls() {
        Product product = new Product();
        product.setImagePath("https://cdn.example.com/images/test.jpg");

        assertThat(product.getImagePath()).isEqualTo("https://cdn.example.com/images/test.jpg");
    }
}
