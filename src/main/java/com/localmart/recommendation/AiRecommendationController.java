package com.localmart.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class AiRecommendationController {

    private final AiRecommendationRepository recommendationRepository;

    @GetMapping
    public List<AiRecommendation> getRecommendations(@RequestParam Long customerId) {
        return recommendationRepository.findByCustomerIdOrderByScoreDesc(customerId);
    }
}
