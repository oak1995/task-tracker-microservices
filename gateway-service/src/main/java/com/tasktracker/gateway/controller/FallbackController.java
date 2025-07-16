package com.tasktracker.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller для Gateway
 * 
 * Обрабатывает запросы когда downstream сервисы недоступны
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);
    
    /**
     * Fallback для Auth Service
     */
    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        logger.warn("Auth Service недоступен - возвращаем fallback ответ");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "AUTH_SERVICE_UNAVAILABLE");
        response.put("message", "Сервис аутентификации временно недоступен");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Попробуйте позже или обратитесь к администратору");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    /**
     * Fallback для Task Service
     */
    @GetMapping("/tasks")
    public Mono<ResponseEntity<Map<String, Object>>> tasksFallback() {
        logger.warn("Task Service недоступен - возвращаем fallback ответ");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "TASK_SERVICE_UNAVAILABLE");
        response.put("message", "Сервис задач временно недоступен");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Попробуйте позже или обратитесь к администратору");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    /**
     * Fallback для Audit Service
     */
    @GetMapping("/audit")
    public Mono<ResponseEntity<Map<String, Object>>> auditFallback() {
        logger.warn("Audit Service недоступен - возвращаем fallback ответ");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "AUDIT_SERVICE_UNAVAILABLE");
        response.put("message", "Сервис аудита временно недоступен");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Попробуйте позже или обратитесь к администратору");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
    
    /**
     * Общий fallback для всех сервисов
     */
    @GetMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        logger.warn("Сервис недоступен - возвращаем общий fallback ответ");
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "SERVICE_UNAVAILABLE");
        response.put("message", "Запрашиваемый сервис временно недоступен");
        response.put("timestamp", LocalDateTime.now());
        response.put("suggestion", "Попробуйте позже или обратитесь к администратору");
        response.put("supportContact", "support@tasktracker.com");
        
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
} 