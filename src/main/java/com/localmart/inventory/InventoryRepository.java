package com.localmart.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByStoreId(Long storeId);
    List<Inventory> findByProductId(Long productId);
    Optional<Inventory> findByProductIdAndStoreId(Long productId, Long storeId);
    List<Inventory> findByProductIdIn(List<Long> productIds);
}
