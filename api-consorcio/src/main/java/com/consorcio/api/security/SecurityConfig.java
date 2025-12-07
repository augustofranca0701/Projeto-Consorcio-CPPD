package com.consorcio.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.http.HttpMethod;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomAuthenticationEntryPoint unauthorizedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomAuthenticationEntryPoint unauthorizedHandler) {
        this.jwtFilter = jwtFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
          .authorizeHttpRequests(auth -> auth
              // libera preflight OPTIONS
              .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

              // === REGISTRO ===
              .requestMatchers(HttpMethod.POST, "/register").permitAll()
              .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

              // === LOGIN (CORREÇÃO AQUI) ===
              .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

              // outras rotas públicas se necessário
              .requestMatchers("/public/**", "/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

              // o resto exige autenticação
              .anyRequest().authenticated()
          );

        // adiciona o filtro JWT antes do filtro padrão de autenticação por username/password
        http.addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        // Caso use H2 console em dev
        http.headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
