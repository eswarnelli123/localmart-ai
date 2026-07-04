package com.localmart.product;

import java.util.Locale;

public final class ProductSearchMatcher {

    private ProductSearchMatcher() {
    }

    public static boolean matches(Product product, String query) {
        if (product == null || query == null || query.isBlank()) {
            return false;
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        String name = product.getName() == null ? "" : product.getName().toLowerCase(Locale.ROOT);
        String description = product.getDescription() == null ? "" : product.getDescription().toLowerCase(Locale.ROOT);
        String sku = product.getSku() == null ? "" : product.getSku().toLowerCase(Locale.ROOT);
        String brand = product.getBrand() == null ? "" : product.getBrand().toLowerCase(Locale.ROOT);
        String categoryName = product.getCategoryId() == null ? "" : product.getCategoryId().toString();
        String shopName = product.getShop() == null || product.getShop().getName() == null
                ? ""
                : product.getShop().getName().toLowerCase(Locale.ROOT);

        // Exact substring match (highest priority)
        if (name.contains(normalizedQuery)
                || description.contains(normalizedQuery)
                || sku.contains(normalizedQuery)
                || brand.contains(normalizedQuery)
                || categoryName.contains(normalizedQuery)
                || shopName.contains(normalizedQuery)) {
            return true;
        }

        // Fuzzy matching: partial tokens, handles plurals and variations
        // Allow matches even if some words are missing (at least 50% match rate)
        String combined = String.join(" ", name, description, sku, brand, categoryName, shopName);
        String[] tokens = normalizedQuery.split("\\s+");
        int matchedTokens = 0;
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            if (fuzzyMatch(combined, token)) {
                matchedTokens++;
            }
        }
        // Return true if at least 50% of tokens match
        // Single word: requires 100% match. 2 words: 1 match enough. 3+ words: allows up to 1-2 missing
        int totalTokens = (int) java.util.Arrays.stream(tokens).filter(t -> !t.isBlank()).count();
        if (totalTokens == 0) {
            return false;
        }
        return matchedTokens >= Math.ceil(totalTokens * 0.5);
    }

    // Fuzzy match: handles plurals (inner/inners) and substring variations
    private static boolean fuzzyMatch(String text, String token) {
        if (text.contains(token)) {
            return true;
        }
        // Remove common plural/singular variations and try again
        String[] variations = new String[]{
                token,
                token.endsWith("s") ? token.substring(0, token.length() - 1) : token + "s",
                token.endsWith("es") ? token.substring(0, token.length() - 2) : null,
                token.endsWith("ies") ? token.substring(0, token.length() - 3) + "y" : null
        };
        for (String variation : variations) {
            if (variation != null && !variation.isBlank() && text.contains(variation)) {
                return true;
            }
        }
        // Check if any token in text starts with the search token (partial match)
        for (String word : text.split(" ")) {
            if (word.startsWith(token) || token.length() >= 3 && word.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
