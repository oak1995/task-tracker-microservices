package com.tasktracker.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Auth Service
 * 
 * @SpringBootApplication - аннотация, которая объединяет:
 * - @Configuration: класс является источником bean-определений
 * - @EnableAutoConfiguration: автоматически настраивает Spring Boot
 * - @ComponentScan: сканирует компоненты в пакете com.tasktracker.auth
 */
@SpringBootApplication
public class AuthServiceApplication {
    
    /**
     * Точка входа в приложение
     * 
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
} 