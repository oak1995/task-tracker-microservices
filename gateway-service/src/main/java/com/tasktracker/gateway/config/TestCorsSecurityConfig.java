package com.tasktracker.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Упрощенная конфигурация безопасности для тестирования CORS
 */
@Configuration
@EnableWebFluxSecurity
@Profile("test-cors")
public class TestCorsSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Отключаем CSRF
                .csrf().disable()
                
                // Включаем CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Разрешаем все запросы для тестирования
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                
                // Отключаем authentication
                .httpBasic().disable()
                .formLogin().disable()
                
                .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Разрешенные источники
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001", 
            "http://localhost:3002",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "file://",
            "null"
        ));
        
        // Разрешенные методы
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Разрешенные заголовки
        configuration.setAllowedHeaders(List.of("*"));
        
        // Разрешить cookies
        configuration.setAllowCredentials(true);
        
        // Максимальный возраст preflight запроса
        configuration.setMaxAge(3600L);
        
        // Заголовки, которые клиент может прочитать
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Access-Control-Max-Age",
            "Access-Control-Allow-Credentials"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 