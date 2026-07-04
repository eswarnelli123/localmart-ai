package com.localmart.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RestAiClient implements AiClient {

    private final RestTemplate rest = new RestTemplate();
    // Configure endpoint and key via application properties or env vars in production
    private final String endpoint = System.getenv().getOrDefault("AI_ENDPOINT", "");
    private final String apiKey = System.getenv().getOrDefault("AI_API_KEY", "");

    @Override
    public String generateText(String prompt) {
        if (endpoint == null || endpoint.isBlank()) {
            log.warn("AI endpoint not configured; returning empty AI result.");
            return null;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (apiKey != null && !apiKey.isBlank()) {
                headers.set("Authorization", "Bearer " + apiKey);
            }
            Map<String, Object> body = new HashMap<>();
            body.put("prompt", prompt);
            body.put("max_tokens", 400);
            HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);
            Map resp = rest.postForObject(endpoint, req, Map.class);
            if (resp == null) return null;
            // Generic parsing — adapt to the provider's response schema.
            Object text = resp.get("text");
            if (text == null) text = resp.get("result");
            return text != null ? text.toString() : resp.toString();
        } catch (Exception e) {
            log.warn("AI call failed: {}", e.getMessage());
            return null;
        }
    }
}
