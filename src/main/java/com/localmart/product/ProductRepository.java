package com.localmart.product;

import com.localmart.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShopIn(List<Shop> shops);
    List<Product> findByShop(Shop shop);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
}
