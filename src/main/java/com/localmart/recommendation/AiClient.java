package com.localmart.recommendation;

public interface AiClient {
    /**
     * Send a prompt/context to an external LLM and return text result.
     * Implementations should use secure API keys from configuration/environment.
     */
    String generateText(String prompt);
}
