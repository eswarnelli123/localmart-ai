package com.localmart.product;

import com.localmart.shop.Shop;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductSearchMatcherTest {

    @Test
    void shouldMatchProductByName() {
        Product product = new Product();
        product.setName("Organic Turmeric Powder");
        product.setDescription("Fresh spice");
        product.setSku("LMG-001");

        assertThat(ProductSearchMatcher.matches(product, "turmeric")).isTrue();
    }

    @Test
    void shouldMatchProductByShopName() {
        Product product = new Product();
        product.setName("Mineral Water");
        Shop shop = new Shop();
        shop.setName("LocalMart Central");
        product.setShop(shop);

        assertThat(ProductSearchMatcher.matches(product, "central")).isTrue();
    }
}
