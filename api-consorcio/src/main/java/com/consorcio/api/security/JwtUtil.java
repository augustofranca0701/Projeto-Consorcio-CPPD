package com.consorcio.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        String adjustedSecret = adjustSecret(secret);
        this.key = Keys.hmacShaKeyFor(adjustedSecret.getBytes());
        this.expirationMs = expirationMs;

        System.out.println("====================================");
        System.out.println(" JWT SECRET CARREGADO ");
        System.out.println(" Tamanho: " + adjustedSecret.length() + " chars");
        System.out.println(" SHA-256 (Base64): " +
                java.util.Base64.getEncoder().encodeToString(key.getEncoded()));
        System.out.println(" Expiração: " + expirationMs + "ms");
        System.out.println("====================================");
    }

    public String generateToken(String subject) {
        if (subject == null) subject = "unknown";

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        return validateToken(token, null);
    }

    public boolean validateToken(String token, String ignored) {
        if (token == null || token.isBlank()) return false;

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private static String adjustSecret(String secret) {
        if (secret == null || secret.isBlank()) {
            secret = "change-this-secret-to-a-long-one";
        }

        StringBuilder sb = new StringBuilder(secret);
        while (sb.toString().getBytes().length < 32) {
            sb.append("0");
        }

        return sb.toString();
    }
}
