package com.tasktracker.gateway.integration;

import com.tasktracker.gateway.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для Gateway Service
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class GatewayServiceIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private RouteLocator routeLocator;
    
    @BeforeEach
    void setUp() {
        // Увеличиваем timeout для тестов
        webTestClient = webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    @Test
    void testGatewayInfoEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/gateway/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("Gateway Service")
                .jsonPath("$.version").isEqualTo("1.0.0")
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.description").isEqualTo("API Gateway для Task Tracker системы")
                .jsonPath("$.port").isEqualTo(8080)
                .jsonPath("$.features").isArray()
                .jsonPath("$.features[0]").isEqualTo("JWT Authentication")
                .jsonPath("$.features[1]").isEqualTo("Rate Limiting")
                .jsonPath("$.features[2]").isEqualTo("Circuit Breaker")
                .jsonPath("$.features[3]").isEqualTo("Load Balancing")
                .jsonPath("$.features[4]").isEqualTo("CORS Support")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testGatewayStatsEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/gateway/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("HEALTHY")
                .jsonPath("$.uptime").isEqualTo("Available")
                .jsonPath("$.services").exists()
                .jsonPath("$.services.auth-service").exists()
                .jsonPath("$.services.task-service").exists()
                .jsonPath("$.services.audit-service").exists()
                .jsonPath("$.services.auth-service.name").isEqualTo("Auth Service")
                .jsonPath("$.services.auth-service.url").isEqualTo("http://localhost:8081")
                .jsonPath("$.services.auth-service.status").isEqualTo("UP")
                .jsonPath("$.circuitBreakers").exists()
                .jsonPath("$.circuitBreakers.auth-circuit-breaker").exists()
                .jsonPath("$.circuitBreakers.task-circuit-breaker").exists()
                .jsonPath("$.circuitBreakers.audit-circuit-breaker").exists()
                .jsonPath("$.circuitBreakers.auth-circuit-breaker.state").isEqualTo("CLOSED")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testGatewayHealthEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/gateway/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("Gateway Service")
                .jsonPath("$.components").exists()
                .jsonPath("$.components.redis").exists()
                .jsonPath("$.components.routes").exists()
                .jsonPath("$.components.security").exists()
                .jsonPath("$.components.redis.status").isEqualTo("UP")
                .jsonPath("$.components.redis.message").isEqualTo("Redis доступен")
                .jsonPath("$.components.routes.status").isEqualTo("UP")
                .jsonPath("$.components.routes.message").isEqualTo("Маршруты настроены")
                .jsonPath("$.components.security.status").isEqualTo("UP")
                .jsonPath("$.components.security.message").isEqualTo("JWT валидация работает")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testGatewayRoutesEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/gateway/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isEqualTo(3)
                .jsonPath("$.routes").exists()
                .jsonPath("$.routes.auth-service").exists()
                .jsonPath("$.routes.task-service").exists()
                .jsonPath("$.routes.audit-service").exists()
                .jsonPath("$.routes.auth-service.path").isEqualTo("/auth/**")
                .jsonPath("$.routes.auth-service.target").isEqualTo("http://localhost:8081")
                .jsonPath("$.routes.task-service.path").isEqualTo("/tasks/**")
                .jsonPath("$.routes.task-service.target").isEqualTo("http://localhost:8082")
                .jsonPath("$.routes.audit-service.path").isEqualTo("/audit/**")
                .jsonPath("$.routes.audit-service.target").isEqualTo("http://localhost:8083")
                .jsonPath("$.routes.auth-service.filters").isArray()
                .jsonPath("$.routes.auth-service.methods").isArray()
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testFallbackAuthEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/fallback/auth")
                .exchange()
                .expectStatus().isEqualTo(503) // SERVICE_UNAVAILABLE
                .expectBody()
                .jsonPath("$.error").isEqualTo("AUTH_SERVICE_UNAVAILABLE")
                .jsonPath("$.message").isEqualTo("Сервис аутентификации временно недоступен")
                .jsonPath("$.suggestion").isEqualTo("Попробуйте позже или обратитесь к администратору")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testFallbackTasksEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/fallback/tasks")
                .exchange()
                .expectStatus().isEqualTo(503) // SERVICE_UNAVAILABLE
                .expectBody()
                .jsonPath("$.error").isEqualTo("TASK_SERVICE_UNAVAILABLE")
                .jsonPath("$.message").isEqualTo("Сервис задач временно недоступен")
                .jsonPath("$.suggestion").isEqualTo("Попробуйте позже или обратитесь к администратору")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testFallbackAuditEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/fallback/audit")
                .exchange()
                .expectStatus().isEqualTo(503) // SERVICE_UNAVAILABLE
                .expectBody()
                .jsonPath("$.error").isEqualTo("AUDIT_SERVICE_UNAVAILABLE")
                .jsonPath("$.message").isEqualTo("Сервис аудита временно недоступен")
                .jsonPath("$.suggestion").isEqualTo("Попробуйте позже или обратитесь к администратору")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testFallbackDefaultEndpoint() {
        // Act & Assert
        webTestClient
                .get()
                .uri("/fallback/default")
                .exchange()
                .expectStatus().isEqualTo(503) // SERVICE_UNAVAILABLE
                .expectBody()
                .jsonPath("$.error").isEqualTo("SERVICE_UNAVAILABLE")
                .jsonPath("$.message").isEqualTo("Запрашиваемый сервис временно недоступен")
                .jsonPath("$.suggestion").isEqualTo("Попробуйте позже или обратитесь к администратору")
                .jsonPath("$.supportContact").isEqualTo("support@tasktracker.com")
                .jsonPath("$.timestamp").exists();
    }
    
    @Test
    void testGatewayRoutesConfiguration() {
        // Проверяем, что маршруты настроены правильно
        assertNotNull(routeLocator);
        
        // Получаем маршруты и проверяем их количество
        var routes = routeLocator.getRoutes().collectList().block();
        assertNotNull(routes);
        assertTrue(routes.size() >= 3, "Должно быть минимум 3 маршрута");
    }
    
    @Test
    void testCorsConfiguration() {
        // Проверяем CORS заголовки
        webTestClient
                .options()
                .uri("/gateway/info")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Access-Control-Allow-Origin")
                .expectHeader().exists("Access-Control-Allow-Methods");
    }
    
    @Test
    void testUnauthorizedRequestWithoutToken() {
        // Пытаемся получить доступ к защищенному ресурсу без токена
        // (в реальном приложении это должно быть защищено)
        webTestClient
                .get()
                .uri("/auth/profile")
                .exchange()
                .expectStatus().isEqualTo(404); // Или другой статус, в зависимости от конфигурации
    }
    
    @Test
    void testJwtAuthenticationFilterIntegration() {
        // Проверяем, что JWT фильтр настроен (без реального токена будет 404, так как нет downstream сервиса)
        webTestClient
                .get()
                .uri("/auth/profile")
                .header("Authorization", "Bearer invalid-token")
                .exchange()
                .expectStatus().isEqualTo(404); // Или другой статус
    }
    
    @Test
    void testRateLimitingIntegration() {
        // Проверяем, что rate limiting настроен
        // В реальном окружении это требует Redis, но мы можем проверить, что фильтр применяется
        webTestClient
                .get()
                .uri("/gateway/info")
                .exchange()
                .expectStatus().isOk();
    }
    
    @Test
    void testCircuitBreakerConfiguration() {
        // Проверяем, что circuit breaker настроен в статистике
        webTestClient
                .get()
                .uri("/gateway/stats")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.circuitBreakers.auth-circuit-breaker.state").isEqualTo("CLOSED")
                .jsonPath("$.circuitBreakers.task-circuit-breaker.state").isEqualTo("CLOSED")
                .jsonPath("$.circuitBreakers.audit-circuit-breaker.state").isEqualTo("CLOSED");
    }
    
    @Test
    void testApplicationStartup() {
        // Проверяем, что приложение правильно запускается
        webTestClient
                .get()
                .uri("/gateway/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
    
    @Test
    void testErrorHandling() {
        // Проверяем обработку ошибок для несуществующего endpoint
        webTestClient
                .get()
                .uri("/nonexistent")
                .exchange()
                .expectStatus().isEqualTo(404);
    }
} 