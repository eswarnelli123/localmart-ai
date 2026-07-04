package com.localmart.offer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferRepository offerRepository;

    @GetMapping
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @PostMapping
    public Offer createOffer(@RequestBody Offer offer) {
        return offerRepository.save(offer);
    }
}
