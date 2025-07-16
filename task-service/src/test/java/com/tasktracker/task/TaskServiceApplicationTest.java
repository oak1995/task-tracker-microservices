package com.tasktracker.task;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовый тест для проверки загрузки Spring Boot контекста
 */
@SpringBootTest
@ActiveProfiles("test")
class TaskServiceApplicationTest {

    @Test
    void contextLoads() {
        // Этот тест проверяет, что Spring Boot контекст загружается успешно
        // Если есть проблемы с конфигурацией, тест упадет
    }
} 