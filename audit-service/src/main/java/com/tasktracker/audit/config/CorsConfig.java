package com.tasktracker.audit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// @Configuration - отключено для избежания конфликтов с Gateway CORS
public class CorsConfig {

    // @Bean - отключено, CORS управляется Gateway Service
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Разрешенные origins
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:3001", 
            "http://localhost:3002",
            "http://localhost:8080"
        ));
        
        // Разрешенные методы
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Разрешенные заголовки
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Разрешить credentials
        configuration.setAllowCredentials(true);
        
        // Время кэширования preflight запросов
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Применить CORS ко всем путям
        source.registerCorsConfiguration("/**", configuration);
        
        // Специально для actuator endpoints
        source.registerCorsConfiguration("/actuator/**", configuration);
        
        return source;
    }
} 