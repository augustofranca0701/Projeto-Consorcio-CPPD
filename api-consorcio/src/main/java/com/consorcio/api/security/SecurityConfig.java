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
            // API stateless → CSRF desligado
            .csrf(csrf -> csrf.disable())

            // Sem sessão
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Handler padrão de 401
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(unauthorizedHandler)
            )

            // Regras de autorização
            .authorizeHttpRequests(auth -> auth
                // AUTH sempre público
                .requestMatchers("/api/auth/**").permitAll()

                // qualquer outra rota exige token
                .anyRequest().authenticated()
            );

        // JWT entra antes do filtro padrão
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
