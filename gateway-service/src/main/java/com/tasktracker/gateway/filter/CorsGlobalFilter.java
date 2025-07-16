package com.tasktracker.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Глобальный CORS фильтр для Spring Cloud Gateway
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        
        HttpHeaders headers = response.getHeaders();
        String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
        
        // Разрешаем CORS для localhost и Docker
        if (origin != null && (origin.startsWith("http://localhost:") || 
                              origin.startsWith("http://127.0.0.1:") ||
                              origin.startsWith("http://host.docker.internal:") ||
                              origin.startsWith("file://") ||
                              origin.equals("null"))) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }
        
        // Устанавливаем CORS заголовки
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, 
                   "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, 
                   "Content-Type, Authorization, X-Requested-With, Origin, Accept");
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        
        // Обрабатываем preflight OPTIONS запросы
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        
        return chain.filter(exchange);
    }
} 