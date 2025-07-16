package com.tasktracker.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для GatewayController
 */
@ExtendWith(MockitoExtension.class)
class GatewayControllerTest {

    private GatewayController gatewayController;
    
    @BeforeEach
    void setUp() {
        gatewayController = new GatewayController();
    }
    
    @Test
    void testGetGatewayInfo() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getGatewayInfo();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    
                    Map<String, Object> info = response.getBody();
                    assertNotNull(info);
                    
                    // Проверяем основные поля
                    assertEquals("Gateway Service", info.get("service"));
                    assertEquals("1.0.0", info.get("version"));
                    assertEquals("UP", info.get("status"));
                    assertEquals("API Gateway для Task Tracker системы", info.get("description"));
                    assertEquals(8080, info.get("port"));
                    
                    // Проверяем timestamp
                    assertNotNull(info.get("timestamp"));
                    assertTrue(info.get("timestamp") instanceof LocalDateTime);
                    
                    // Проверяем features
                    assertNotNull(info.get("features"));
                    assertTrue(info.get("features") instanceof String[]);
                    String[] features = (String[]) info.get("features");
                    assertEquals(5, features.length);
                    assertTrue(containsFeature(features, "JWT Authentication"));
                    assertTrue(containsFeature(features, "Rate Limiting"));
                    assertTrue(containsFeature(features, "Circuit Breaker"));
                    assertTrue(containsFeature(features, "Load Balancing"));
                    assertTrue(containsFeature(features, "CORS Support"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testGetStats() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getStats();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    
                    Map<String, Object> stats = response.getBody();
                    assertNotNull(stats);
                    
                    // Проверяем основные поля
                    assertNotNull(stats.get("timestamp"));
                    assertEquals("Available", stats.get("uptime"));
                    assertEquals("HEALTHY", stats.get("status"));
                    
                    // Проверяем services
                    assertNotNull(stats.get("services"));
                    assertTrue(stats.get("services") instanceof Map);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> services = (Map<String, Object>) stats.get("services");
                    
                    assertTrue(services.containsKey("auth-service"));
                    assertTrue(services.containsKey("task-service"));
                    assertTrue(services.containsKey("audit-service"));
                    
                    // Проверяем circuit breakers
                    assertNotNull(stats.get("circuitBreakers"));
                    assertTrue(stats.get("circuitBreakers") instanceof Map);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> circuitBreakers = (Map<String, Object>) stats.get("circuitBreakers");
                    
                    assertTrue(circuitBreakers.containsKey("auth-circuit-breaker"));
                    assertTrue(circuitBreakers.containsKey("task-circuit-breaker"));
                    assertTrue(circuitBreakers.containsKey("audit-circuit-breaker"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testHealth() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.health();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    
                    Map<String, Object> health = response.getBody();
                    assertNotNull(health);
                    
                    // Проверяем основные поля
                    assertEquals("UP", health.get("status"));
                    assertEquals("Gateway Service", health.get("service"));
                    assertNotNull(health.get("timestamp"));
                    
                    // Проверяем components
                    assertNotNull(health.get("components"));
                    assertTrue(health.get("components") instanceof Map);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> components = (Map<String, Object>) health.get("components");
                    
                    assertTrue(components.containsKey("redis"));
                    assertTrue(components.containsKey("routes"));
                    assertTrue(components.containsKey("security"));
                    
                    // Проверяем каждый компонент
                    @SuppressWarnings("unchecked")
                    Map<String, Object> redis = (Map<String, Object>) components.get("redis");
                    assertEquals("UP", redis.get("status"));
                    assertEquals("Redis доступен", redis.get("message"));
                    assertNotNull(redis.get("timestamp"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> routes = (Map<String, Object>) components.get("routes");
                    assertEquals("UP", routes.get("status"));
                    assertEquals("Маршруты настроены", routes.get("message"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> security = (Map<String, Object>) components.get("security");
                    assertEquals("UP", security.get("status"));
                    assertEquals("JWT валидация работает", security.get("message"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testGetRoutes() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getRoutes();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    
                    Map<String, Object> routesInfo = response.getBody();
                    assertNotNull(routesInfo);
                    
                    // Проверяем основные поля
                    assertNotNull(routesInfo.get("timestamp"));
                    assertEquals(3, routesInfo.get("count"));
                    
                    // Проверяем routes
                    assertNotNull(routesInfo.get("routes"));
                    assertTrue(routesInfo.get("routes") instanceof Map);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> routes = (Map<String, Object>) routesInfo.get("routes");
                    
                    assertTrue(routes.containsKey("auth-service"));
                    assertTrue(routes.containsKey("task-service"));
                    assertTrue(routes.containsKey("audit-service"));
                    
                    // Проверяем auth-service route
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authRoute = (Map<String, Object>) routes.get("auth-service");
                    assertEquals("/auth/**", authRoute.get("path"));
                    assertEquals("http://localhost:8081", authRoute.get("target"));
                    assertNotNull(authRoute.get("filters"));
                    assertNotNull(authRoute.get("methods"));
                    
                    // Проверяем task-service route
                    @SuppressWarnings("unchecked")
                    Map<String, Object> taskRoute = (Map<String, Object>) routes.get("task-service");
                    assertEquals("/tasks/**", taskRoute.get("path"));
                    assertEquals("http://localhost:8082", taskRoute.get("target"));
                    
                    // Проверяем audit-service route
                    @SuppressWarnings("unchecked")
                    Map<String, Object> auditRoute = (Map<String, Object>) routes.get("audit-service");
                    assertEquals("/audit/**", auditRoute.get("path"));
                    assertEquals("http://localhost:8083", auditRoute.get("target"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testGetStatsServiceStructure() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getStats();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> stats = response.getBody();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> services = (Map<String, Object>) stats.get("services");
                    
                    // Проверяем структуру каждого сервиса
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authService = (Map<String, Object>) services.get("auth-service");
                    assertEquals("Auth Service", authService.get("name"));
                    assertEquals("http://localhost:8081", authService.get("url"));
                    assertEquals("UP", authService.get("status"));
                    assertEquals("N/A", authService.get("responseTime"));
                    assertNotNull(authService.get("lastCheck"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> taskService = (Map<String, Object>) services.get("task-service");
                    assertEquals("Task Service", taskService.get("name"));
                    assertEquals("http://localhost:8082", taskService.get("url"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> auditService = (Map<String, Object>) services.get("audit-service");
                    assertEquals("Audit Service", auditService.get("name"));
                    assertEquals("http://localhost:8083", auditService.get("url"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testGetStatsCircuitBreakerStructure() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getStats();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> stats = response.getBody();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> circuitBreakers = (Map<String, Object>) stats.get("circuitBreakers");
                    
                    // Проверяем структуру каждого circuit breaker
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authCB = (Map<String, Object>) circuitBreakers.get("auth-circuit-breaker");
                    assertEquals("CLOSED", authCB.get("state"));
                    assertEquals(0.0, authCB.get("failureRate"));
                    assertEquals(0, authCB.get("calls"));
                    assertNotNull(authCB.get("lastUpdated"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> taskCB = (Map<String, Object>) circuitBreakers.get("task-circuit-breaker");
                    assertEquals("CLOSED", taskCB.get("state"));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> auditCB = (Map<String, Object>) circuitBreakers.get("audit-circuit-breaker");
                    assertEquals("CLOSED", auditCB.get("state"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testGetRoutesStructure() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = gatewayController.getRoutes();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> routesInfo = response.getBody();
                    @SuppressWarnings("unchecked")
                    Map<String, Object> routes = (Map<String, Object>) routesInfo.get("routes");
                    
                    // Проверяем структуру каждого маршрута
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authRoute = (Map<String, Object>) routes.get("auth-service");
                    String[] authFilters = (String[]) authRoute.get("filters");
                    assertEquals(3, authFilters.length);
                    assertTrue(containsFilter(authFilters, "CircuitBreaker"));
                    assertTrue(containsFilter(authFilters, "Retry"));
                    assertTrue(containsFilter(authFilters, "AuthenticationFilter"));
                    
                    String[] authMethods = (String[]) authRoute.get("methods");
                    assertEquals(4, authMethods.length);
                    assertTrue(containsMethod(authMethods, "GET"));
                    assertTrue(containsMethod(authMethods, "POST"));
                    assertTrue(containsMethod(authMethods, "PUT"));
                    assertTrue(containsMethod(authMethods, "DELETE"));
                })
                .expectComplete()
                .verify();
    }
    
    // Вспомогательные методы
    private boolean containsFeature(String[] features, String feature) {
        for (String f : features) {
            if (f.equals(feature)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsFilter(String[] filters, String filter) {
        for (String f : filters) {
            if (f.equals(filter)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsMethod(String[] methods, String method) {
        for (String m : methods) {
            if (m.equals(method)) {
                return true;
            }
        }
        return false;
    }
} 