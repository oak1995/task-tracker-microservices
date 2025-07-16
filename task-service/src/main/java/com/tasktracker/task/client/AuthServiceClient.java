package com.tasktracker.task.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign клиент для интеграции с Auth Service
 * 
 * Этот интерфейс определяет методы для взаимодействия с Auth Service.
 * Spring Cloud OpenFeign автоматически создает реализацию этого интерфейса
 * и обрабатывает HTTP запросы к Auth Service.
 * 
 * Важные особенности микросервисной интеграции:
 * 1. Декларативный стиль - описываем что хотим, а не как делать
 * 2. Автоматическая сериализация/десериализация JSON
 * 3. Обработка ошибок и повторных попыток
 * 4. Балансировка нагрузки между экземплярами сервиса
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@FeignClient(
    name = "auth-service",
    url = "${auth.service.url}",
    configuration = FeignClientConfig.class
)
public interface AuthServiceClient {
    
    /**
     * Валидация JWT токена
     * 
     * Отправляет запрос к Auth Service для проверки валидности JWT токена
     * и получения информации о пользователе.
     * 
     * @param token JWT токен в формате "Bearer <token>"
     * @return информация о пользователе, если токен валиден
     */
    @GetMapping("/validate")
    UserInfo validateToken(@RequestHeader("Authorization") String token);
    
    /**
     * Получение информации о пользователе по ID
     * 
     * @param userId ID пользователя
     * @param token JWT токен для авторизации
     * @return информация о пользователе
     */
    @GetMapping("/users/{userId}")
    UserInfo getUserById(@RequestHeader("Authorization") String token, Long userId);
    
    /**
     * DTO для информации о пользователе
     * 
     * Этот класс представляет данные о пользователе, которые возвращает Auth Service.
     * Мы используем только те поля, которые нужны для Task Service.
     */
    class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role;
        private Boolean enabled;
        
        // Constructors
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String email, String role, Boolean enabled) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.enabled = enabled;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
        
        @Override
        public String toString() {
            return "UserInfo{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", role='" + role + '\'' +
                    ", enabled=" + enabled +
                    '}';
        }
    }
} 