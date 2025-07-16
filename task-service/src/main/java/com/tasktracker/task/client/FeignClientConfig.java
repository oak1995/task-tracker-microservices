package com.tasktracker.task.client;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация для Feign клиентов
 * 
 * Этот класс настраивает поведение Feign клиентов:
 * - Таймауты для HTTP запросов
 * - Политику повторных попыток
 * - Логирование запросов
 * - Обработку ошибок
 * 
 * Важно для production:
 * - Установить разумные таймауты
 * - Настроить retry логику
 * - Логировать для отладки
 * - Graceful обработка ошибок
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Configuration
public class FeignClientConfig {
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignClientConfig.class);
    
    /**
     * Настройка таймаутов для HTTP запросов
     * 
     * connectTimeout - время ожидания установки соединения
     * readTimeout - время ожидания ответа от сервера
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                5000,  // connectTimeout = 5 секунд
                10000, // readTimeout = 10 секунд
                true   // followRedirects = true
        );
    }
    
    /**
     * Настройка политики повторных попыток
     * 
     * При сбое запроса Feign будет повторять попытки
     * с экспоненциальной задержкой.
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
                1000,  // период между попытками = 1 секунда
                3000,  // максимальный период = 3 секунды
                3      // максимальное количество попыток = 3
        );
    }
    
    /**
     * Настройка логирования Feign запросов
     * 
     * BASIC - логирует метод, URL и код ответа
     * FULL - логирует все детали запроса и ответа
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    /**
     * Кастомный декодер ошибок
     * 
     * Преобразует HTTP ошибки в соответствующие Java исключения
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
    
    /**
     * Кастомный обработчик ошибок для Feign
     * 
     * Этот класс определяет, как обрабатывать различные HTTP ошибки
     * от внешних сервисов.
     */
    public static class FeignErrorDecoder implements ErrorDecoder {
        
        private final ErrorDecoder defaultErrorDecoder = new Default();
        
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            logger.error("Feign client error: method={}, status={}, reason={}", 
                    methodKey, response.status(), response.reason());
            
            switch (response.status()) {
                case 400:
                    return new IllegalArgumentException("Bad request to " + methodKey);
                case 401:
                    return new SecurityException("Unauthorized access to " + methodKey);
                case 403:
                    return new SecurityException("Access forbidden to " + methodKey);
                case 404:
                    return new RuntimeException("Resource not found: " + methodKey);
                case 500:
                    return new RuntimeException("Internal server error in " + methodKey);
                case 503:
                    return new RuntimeException("Service unavailable: " + methodKey);
                default:
                    return defaultErrorDecoder.decode(methodKey, response);
            }
        }
    }
} 