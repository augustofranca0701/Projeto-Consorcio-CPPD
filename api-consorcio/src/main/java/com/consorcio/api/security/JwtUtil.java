package com.consorcio.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration-ms:86400000}") long expirationMs
    ) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret NÃO pode ser vazio ou nulo");
        }

        String adjustedSecret = adjustSecret(secret);
        this.key = Keys.hmacShaKeyFor(adjustedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;

        System.out.println("====================================");
        System.out.println(" JWT SECRET CARREGADO COM SUCESSO ");
        System.out.println(" Tamanho (chars): " + adjustedSecret.length());
        System.out.println(" Tamanho (bytes): " + adjustedSecret.getBytes(StandardCharsets.UTF_8).length);
        System.out.println(" Expiração: " + expirationMs + "ms");
        System.out.println("====================================");
    }

    public String generateToken(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject do token não pode ser vazio");
        }

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) return false;

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException ex) {
            System.out.println("JWT expirado");
            return false;

        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("JWT inválido: " + ex.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        if (token == null || token.isBlank()) return null;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Erro ao extrair subject do JWT: " + ex.getMessage());
            return null;
        }
    }

    private static String adjustSecret(String secret) {
        StringBuilder sb = new StringBuilder(secret);
        while (sb.toString().getBytes(StandardCharsets.UTF_8).length < 32) {
            sb.append("0");
        }
        return sb.toString();
    }
}
