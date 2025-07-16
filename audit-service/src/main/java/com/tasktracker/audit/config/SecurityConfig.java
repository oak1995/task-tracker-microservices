package com.tasktracker.audit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Autowired - отключено, CORS управляется Gateway Service
    // private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Отключаем CSRF для REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS отключен - управляется Gateway Service
            .cors(cors -> cors.disable())
            
            // Настраиваем авторизацию
            .authorizeHttpRequests(authorize -> authorize
                // Публичные endpoints
                .requestMatchers(
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/audit/**"
                ).permitAll()
                
                // Все остальные endpoints требуют аутентификации
                .anyRequest().authenticated()
            )
            
            // Отключаем сессии (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }
} 