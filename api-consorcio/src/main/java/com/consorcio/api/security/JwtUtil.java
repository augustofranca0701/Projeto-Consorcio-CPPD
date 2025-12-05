package com.consorcio.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Implementação JWT usando jjwt. Lê secret e expiration-ms de properties.
 * Compatível com chamadas existentes:
 * - generateToken(String)
 * - validateToken(String)
 * - validateToken(String, String)
 * - getUsernameFromToken(String)
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${spring.jwt.secret:change-this-secret-please-change}") String secret,
            @Value("${spring.jwt.expiration-ms:86400000}") long expirationMs // default 1 dia
    ) {
        this.key = Keys.hmacShaKeyFor(adjustSecret(secret).getBytes());
        this.expirationMs = expirationMs;
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

    // compatibilidade com assinatura de duas args
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
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private static String adjustSecret(String secret) {
        if (secret == null) secret = "change-this-secret-to-a-long-one";
        // garante pelo menos 32 bytes para HS256
        while (secret.getBytes().length < 32) {
            secret = secret + "0";
        }
        return secret;
    }
}
