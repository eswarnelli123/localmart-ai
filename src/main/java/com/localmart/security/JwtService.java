package com.localmart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:change_me_localmart_ai_change_me_localmart_ai}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        io.jsonwebtoken.JwtParserBuilder builder = Jwts.parserBuilder();
        builder.setSigningKey(getSigningKey());
        io.jsonwebtoken.JwtParser parser = builder.build();
        return parser.parseClaimsJws(token).getBody();
    }

    private SecretKey getSigningKey() {
        String effectiveSecret = (secretKey == null || secretKey.isBlank())
                ? "change_me_localmart_ai_change_me_localmart_ai"
                : secretKey;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(effectiveSecret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(Arrays.copyOf(keyBytes, 32));
        } catch (NoSuchAlgorithmException ex) {
            byte[] keyBytes = effectiveSecret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(Arrays.copyOf(keyBytes, 32));
        }
    }
}
