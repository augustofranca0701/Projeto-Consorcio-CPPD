package com.consorcio.api.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    private boolean isPublicPath(String path) {
        if (path == null) return false;

        return path.startsWith("/public/")
                || path.startsWith("/h2-console/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Incoming request: {} {}", method, path);

        // ==== LIBERAÇÃO DE ENDPOINTS SEM TOKEN ====
        if ("OPTIONS".equalsIgnoreCase(method)
                || isPublicPath(path)

                // REGISTER
                || ("/register".equals(path) && "POST".equalsIgnoreCase(method))
                || ("/auth/register".equals(path) && "POST".equalsIgnoreCase(method))
                || ("/api/auth/register".equals(path) && "POST".equalsIgnoreCase(method))

                // LOGIN  (CORREÇÃO IMPORTANTE)
                || ("/auth/login".equals(path) && "POST".equalsIgnoreCase(method))
                || ("/api/auth/login".equals(path) && "POST".equalsIgnoreCase(method))
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        // ============================================


        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (userDetails != null) {
                            UsernamePasswordAuthenticationToken authToken =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());

                            authToken.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } else {
                    logger.debug("JWT inválido para request {} {}", method, path);
                }
            } else {
                logger.debug("Nenhum Authorization header presente para request {} {}", method, path);
            }
        } catch (Exception ex) {
            logger.warn("Erro ao processar JWT: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response);
    }
}
