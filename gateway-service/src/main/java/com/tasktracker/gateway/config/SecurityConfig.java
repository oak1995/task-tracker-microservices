package com.tasktracker.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


/**
 * Конфигурация безопасности для Gateway Service
 * 
 * Настраивает правила безопасности для Spring Cloud Gateway
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    
    /**
     * Основная конфигурация безопасности
     * 
     * @param http ServerHttpSecurity для WebFlux
     * @return SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Отключаем CSRF для REST API
                .csrf().disable()
                
                // CORS настраивается через CorsGlobalFilter
                .cors(cors -> cors.disable())
                
                // Настраиваем авторизацию
                .authorizeExchange(exchanges -> exchanges
                        // Полностью открываем все endpoints для тестирования CORS
                        .anyExchange().permitAll()
                )
                
                // Настраиваем обработку ошибок
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        })
                )
                
                .build();
    }
    

} 