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
 * Тесты для FallbackController
 */
@ExtendWith(MockitoExtension.class)
class FallbackControllerTest {

    private FallbackController fallbackController;
    
    @BeforeEach
    void setUp() {
        fallbackController = new FallbackController();
    }
    
    @Test
    void testAuthFallback() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.authFallback();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
                    
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    
                    // Проверяем основные поля
                    assertEquals("AUTH_SERVICE_UNAVAILABLE", body.get("error"));
                    assertEquals("Сервис аутентификации временно недоступен", body.get("message"));
                    assertEquals("Попробуйте позже или обратитесь к администратору", body.get("suggestion"));
                    
                    // Проверяем timestamp
                    assertNotNull(body.get("timestamp"));
                    assertTrue(body.get("timestamp") instanceof LocalDateTime);
                    
                    // Проверяем что timestamp недавний (в пределах 1 секунды)
                    LocalDateTime timestamp = (LocalDateTime) body.get("timestamp");
                    LocalDateTime now = LocalDateTime.now();
                    assertTrue(timestamp.isBefore(now.plusSeconds(1)));
                    assertTrue(timestamp.isAfter(now.minusSeconds(1)));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testTasksFallback() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.tasksFallback();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
                    
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    
                    // Проверяем основные поля
                    assertEquals("TASK_SERVICE_UNAVAILABLE", body.get("error"));
                    assertEquals("Сервис задач временно недоступен", body.get("message"));
                    assertEquals("Попробуйте позже или обратитесь к администратору", body.get("suggestion"));
                    
                    // Проверяем timestamp
                    assertNotNull(body.get("timestamp"));
                    assertTrue(body.get("timestamp") instanceof LocalDateTime);
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testAuditFallback() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.auditFallback();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
                    
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    
                    // Проверяем основные поля
                    assertEquals("AUDIT_SERVICE_UNAVAILABLE", body.get("error"));
                    assertEquals("Сервис аудита временно недоступен", body.get("message"));
                    assertEquals("Попробуйте позже или обратитесь к администратору", body.get("suggestion"));
                    
                    // Проверяем timestamp
                    assertNotNull(body.get("timestamp"));
                    assertTrue(body.get("timestamp") instanceof LocalDateTime);
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testDefaultFallback() {
        // Act
        Mono<ResponseEntity<Map<String, Object>>> result = fallbackController.defaultFallback();
        
        // Assert
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
                    
                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);
                    
                    // Проверяем основные поля
                    assertEquals("SERVICE_UNAVAILABLE", body.get("error"));
                    assertEquals("Запрашиваемый сервис временно недоступен", body.get("message"));
                    assertEquals("Попробуйте позже или обратитесь к администратору", body.get("suggestion"));
                    assertEquals("support@tasktracker.com", body.get("supportContact"));
                    
                    // Проверяем timestamp
                    assertNotNull(body.get("timestamp"));
                    assertTrue(body.get("timestamp") instanceof LocalDateTime);
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testAllFallbacksReturnServiceUnavailable() {
        // Act & Assert для всех fallback методов
        StepVerifier.create(fallbackController.authFallback())
                .assertNext(response -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()))
                .expectComplete()
                .verify();
                
        StepVerifier.create(fallbackController.tasksFallback())
                .assertNext(response -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()))
                .expectComplete()
                .verify();
                
        StepVerifier.create(fallbackController.auditFallback())
                .assertNext(response -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()))
                .expectComplete()
                .verify();
                
        StepVerifier.create(fallbackController.defaultFallback())
                .assertNext(response -> assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode()))
                .expectComplete()
                .verify();
    }
    
    @Test
    void testAllFallbacksHaveRequiredFields() {
        // Проверяем, что все fallback методы имеют необходимые поля
        
        // Auth fallback
        StepVerifier.create(fallbackController.authFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertTrue(body.containsKey("error"));
                    assertTrue(body.containsKey("message"));
                    assertTrue(body.containsKey("timestamp"));
                    assertTrue(body.containsKey("suggestion"));
                })
                .expectComplete()
                .verify();
        
        // Tasks fallback
        StepVerifier.create(fallbackController.tasksFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertTrue(body.containsKey("error"));
                    assertTrue(body.containsKey("message"));
                    assertTrue(body.containsKey("timestamp"));
                    assertTrue(body.containsKey("suggestion"));
                })
                .expectComplete()
                .verify();
        
        // Audit fallback
        StepVerifier.create(fallbackController.auditFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertTrue(body.containsKey("error"));
                    assertTrue(body.containsKey("message"));
                    assertTrue(body.containsKey("timestamp"));
                    assertTrue(body.containsKey("suggestion"));
                })
                .expectComplete()
                .verify();
        
        // Default fallback
        StepVerifier.create(fallbackController.defaultFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    assertTrue(body.containsKey("error"));
                    assertTrue(body.containsKey("message"));
                    assertTrue(body.containsKey("timestamp"));
                    assertTrue(body.containsKey("suggestion"));
                    assertTrue(body.containsKey("supportContact"));
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testFallbackErrorCodesAreUnique() {
        // Проверяем, что коды ошибок уникальны для каждого сервиса
        
        String authError = null;
        String taskError = null;
        String auditError = null;
        String defaultError = null;
        
        // Получаем коды ошибок
        StepVerifier.create(fallbackController.authFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String error = (String) body.get("error");
                    assertEquals("AUTH_SERVICE_UNAVAILABLE", error);
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.tasksFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String error = (String) body.get("error");
                    assertEquals("TASK_SERVICE_UNAVAILABLE", error);
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.auditFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String error = (String) body.get("error");
                    assertEquals("AUDIT_SERVICE_UNAVAILABLE", error);
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.defaultFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String error = (String) body.get("error");
                    assertEquals("SERVICE_UNAVAILABLE", error);
                })
                .expectComplete()
                .verify();
    }
    
    @Test
    void testFallbackMessagesAreServiceSpecific() {
        // Проверяем, что сообщения специфичны для каждого сервиса
        
        StepVerifier.create(fallbackController.authFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String message = (String) body.get("message");
                    assertTrue(message.contains("аутентификации"));
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.tasksFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String message = (String) body.get("message");
                    assertTrue(message.contains("задач"));
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.auditFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String message = (String) body.get("message");
                    assertTrue(message.contains("аудита"));
                })
                .expectComplete()
                .verify();
        
        StepVerifier.create(fallbackController.defaultFallback())
                .assertNext(response -> {
                    Map<String, Object> body = response.getBody();
                    String message = (String) body.get("message");
                    assertTrue(message.contains("сервис"));
                })
                .expectComplete()
                .verify();
    }
} 