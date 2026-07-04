package com.localmart.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest req) {
        SearchResult res = searchService.search(req);
        return ResponseEntity.ok(res);
    }
}
