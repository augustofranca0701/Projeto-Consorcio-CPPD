package com.consorcio.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Implementação JWT usando jjwt.
 * Lê o secret e expiration-ms de application-*.yml (que por sua vez lê do .env).
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        // Ajusta o secret para no mínimo 32 bytes, como exige HS256
        String adjustedSecret = adjustSecret(secret);
        this.key = Keys.hmacShaKeyFor(adjustedSecret.getBytes());
        this.expirationMs = expirationMs;

        // LOG ÚTIL PARA DEBUG — mostra o tamanho do secret e a hash
        System.out.println("====================================");
        System.out.println(" JWT SECRET CARREGADO ");
        System.out.println(" Tamanho: " + adjustedSecret.length() + " chars");
        System.out.println(" SHA-256 (Base64): " + java.util.Base64.getEncoder().encodeToString(key.getEncoded()));
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

    // compatibilidade com assinatura antiga
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
            Claims claims =
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Garante que o secret tenha no mínimo 32 bytes — requisito para HS256.
     */
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
