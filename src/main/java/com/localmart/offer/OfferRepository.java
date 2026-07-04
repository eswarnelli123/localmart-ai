package com.localmart.offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByStoreIn(List<com.localmart.shop.Shop> stores);
    List<Offer> findByActiveTrue();
}
