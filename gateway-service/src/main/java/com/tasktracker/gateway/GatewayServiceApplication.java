package com.tasktracker.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Gateway Service Application
 * 
 * Этот класс запускает API Gateway для Task Tracker системы.
 * 
 * Основные функции:
 * - Маршрутизация запросов к микросервисам
 * - Аутентификация и авторизация
 * - Load balancing
 * - Circuit breaker
 * - Мониторинг и метрики
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class GatewayServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
} 