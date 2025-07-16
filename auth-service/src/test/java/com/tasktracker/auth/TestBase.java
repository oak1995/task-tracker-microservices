package com.tasktracker.auth;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Базовый класс для всех тестов
 * 
 * Содержит общие настройки и конфигурацию для тестовой среды
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class TestBase {
    
    /**
     * Настройка, выполняемая перед каждым тестом
     */
    @BeforeEach
    public void setUp() {
        // Общие настройки для всех тестов
        System.setProperty("spring.profiles.active", "test");
    }
} 