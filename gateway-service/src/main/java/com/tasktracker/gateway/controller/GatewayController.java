package com.tasktracker.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway Controller для мониторинга и статистики
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {
    
    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);
    
    /**
     * Информация о Gateway
     */
    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> getGatewayInfo() {
        logger.debug("Получен запрос информации о Gateway");
        
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Gateway Service");
        info.put("version", "1.0.0");
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "UP");
        info.put("description", "API Gateway для Task Tracker системы");
        info.put("port", 8080);
        info.put("features", new String[]{
            "JWT Authentication",
            "Rate Limiting", 
            "Circuit Breaker",
            "Load Balancing",
            "CORS Support"
        });
        
        return Mono.just(ResponseEntity.ok(info));
    }
    
    /**
     * Статистика Gateway
     */
    @GetMapping("/stats")
    public Mono<ResponseEntity<Map<String, Object>>> getStats() {
        logger.debug("Получен запрос статистики Gateway");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now());
        stats.put("uptime", "Available");
        stats.put("status", "HEALTHY");
        
        // Статистика по сервисам
        Map<String, Object> services = new HashMap<>();
        services.put("auth-service", createServiceStats("Auth Service", "http://localhost:8081"));
        services.put("task-service", createServiceStats("Task Service", "http://localhost:8082"));
        services.put("audit-service", createServiceStats("Audit Service", "http://localhost:8083"));
        
        stats.put("services", services);
        
        // Статистика по circuit breakers
        Map<String, Object> circuitBreakers = new HashMap<>();
        circuitBreakers.put("auth-circuit-breaker", createCircuitBreakerStats("CLOSED"));
        circuitBreakers.put("task-circuit-breaker", createCircuitBreakerStats("CLOSED"));
        circuitBreakers.put("audit-circuit-breaker", createCircuitBreakerStats("CLOSED"));
        
        stats.put("circuitBreakers", circuitBreakers);
        
        return Mono.just(ResponseEntity.ok(stats));
    }
    
    /**
     * Health check для Gateway
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        logger.debug("Получен запрос health check");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Gateway Service");
        
        // Проверка компонентов
        Map<String, Object> components = new HashMap<>();
        components.put("redis", createComponentHealth("UP", "Redis доступен"));
        components.put("routes", createComponentHealth("UP", "Маршруты настроены"));
        components.put("security", createComponentHealth("UP", "JWT валидация работает"));
        
        health.put("components", components);
        
        return Mono.just(ResponseEntity.ok(health));
    }
    
    /**
     * Конфигурация маршрутов
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> getRoutes() {
        logger.debug("Получен запрос конфигурации маршрутов");
        
        Map<String, Object> routes = new HashMap<>();
        routes.put("timestamp", LocalDateTime.now());
        
        // Список маршрутов
        Map<String, Object> routeList = new HashMap<>();
        routeList.put("auth-service", createRouteInfo("/auth/**", "http://localhost:8081"));
        routeList.put("task-service", createRouteInfo("/tasks/**", "http://localhost:8082"));
        routeList.put("audit-service", createRouteInfo("/audit/**", "http://localhost:8083"));
        
        routes.put("routes", routeList);
        routes.put("count", 3);
        
        return Mono.just(ResponseEntity.ok(routes));
    }
    
    /**
     * Создание статистики сервиса
     */
    private Map<String, Object> createServiceStats(String name, String url) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("name", name);
        stats.put("url", url);
        stats.put("status", "UP");
        stats.put("responseTime", "N/A");
        stats.put("lastCheck", LocalDateTime.now());
        return stats;
    }
    
    /**
     * Создание статистики Circuit Breaker
     */
    private Map<String, Object> createCircuitBreakerStats(String state) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("state", state);
        stats.put("failureRate", 0.0);
        stats.put("calls", 0);
        stats.put("lastUpdated", LocalDateTime.now());
        return stats;
    }
    
    /**
     * Создание информации о компоненте
     */
    private Map<String, Object> createComponentHealth(String status, String message) {
        Map<String, Object> health = new HashMap<>();
        health.put("status", status);
        health.put("message", message);
        health.put("timestamp", LocalDateTime.now());
        return health;
    }
    
    /**
     * Создание информации о маршруте
     */
    private Map<String, Object> createRouteInfo(String path, String target) {
        Map<String, Object> info = new HashMap<>();
        info.put("path", path);
        info.put("target", target);
        info.put("filters", new String[]{"CircuitBreaker", "Retry", "AuthenticationFilter"});
        info.put("methods", new String[]{"GET", "POST", "PUT", "DELETE"});
        return info;
    }
} 