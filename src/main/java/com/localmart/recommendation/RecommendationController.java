package com.localmart.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationResponse> recommend(@RequestBody RecommendationRequest req) {
        RecommendationResponse res = recommendationService.recommend(req);
        return ResponseEntity.ok(res);
    }
}
