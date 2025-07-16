package com.tasktracker.auth.dto;

import com.tasktracker.auth.entity.Role;

/**
 * DTO для ответа после успешной аутентификации
 * 
 * Возвращается в методах:
 * - POST /auth/login
 * - POST /auth/register
 * 
 * Содержит JWT токен и основную информацию о пользователе
 */
public class AuthResponse {
    
    /**
     * JWT токен для авторизации
     * 
     * Клиент должен отправлять этот токен в заголовке Authorization
     * в формате: "Bearer <token>"
     */
    private String token;
    
    /**
     * Тип токена (всегда "Bearer")
     * 
     * Стандартный тип для JWT токенов
     */
    private String tokenType = "Bearer";
    
    /**
     * ID пользователя
     * 
     * Может быть полезен для клиентских приложений
     */
    private Long userId;
    
    /**
     * Имя пользователя
     * 
     * Для отображения в интерфейсе
     */
    private String username;
    
    /**
     * Email пользователя
     * 
     * Для отображения в интерфейсе
     */
    private String email;
    
    /**
     * Роль пользователя
     * 
     * Для определения прав доступа на клиенте
     */
    private Role role;
    
    // Конструкторы
    public AuthResponse() {
        // Пустой конструктор для Jackson (JSON сериализация)
    }
    
    public AuthResponse(String token, Long userId, String username, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }
    
    // Getters и Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='[PROTECTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
} 