package com.localmart.search;

import lombok.Data;
import java.util.List;

@Data
public class SearchResult {
    private List<StoreResult> stores;
    private List<ProductResult> products;
}
