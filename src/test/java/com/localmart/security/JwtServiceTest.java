package com.localmart.security;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void prodPropertiesShouldProvideJwtFallbacks() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = new ClassPathResource("application-prod.properties").getInputStream()) {
            properties.load(inputStream);
        }

        assertTrue(properties.getProperty("jwt.secret").contains("JWT_SECRET:"), "jwt.secret should provide a fallback value");
        assertTrue(properties.getProperty("jwt.expiration").contains("JWT_EXPIRATION:"), "jwt.expiration should provide a fallback value");
    }
}
