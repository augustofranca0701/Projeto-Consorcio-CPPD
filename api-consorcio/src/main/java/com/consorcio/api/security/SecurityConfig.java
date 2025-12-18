package com.consorcio.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomAuthenticationEntryPoint unauthorizedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            CustomAuthenticationEntryPoint unauthorizedHandler
    ) {
        this.jwtFilter = jwtFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 🔴 API stateless → CSRF OFF
            .csrf(csrf -> csrf.disable())

            // 🔴 JWT → sem sessão
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔴 resposta padronizada para 401
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(unauthorizedHandler)
            )

            // 🔐 Regras de acesso
            .authorizeHttpRequests(auth -> auth

                // ========= ROTAS PÚBLICAS =========
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // ========= TODO O RESTO =========
                .anyRequest().authenticated()
            );

        // 🔴 JWT entra ANTES do UsernamePasswordAuthenticationFilter
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}
