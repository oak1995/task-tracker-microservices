package com.tasktracker.auth.dto;

import com.tasktracker.auth.entity.Role;

import java.time.LocalDateTime;

/**
 * DTO для возврата информации о пользователе
 * 
 * Используется в методах:
 * - GET /auth/profile
 * - GET /auth/users/{id}
 * 
 * НЕ содержит чувствительных данных (паролей, токенов)
 */
public class UserResponse {
    
    /**
     * ID пользователя
     */
    private Long id;
    
    /**
     * Имя пользователя
     */
    private String username;
    
    /**
     * Email пользователя
     */
    private String email;
    
    /**
     * Роль пользователя
     */
    private Role role;
    
    /**
     * Дата создания аккаунта
     */
    private LocalDateTime createdAt;
    
    /**
     * Дата последнего обновления
     */
    private LocalDateTime updatedAt;
    
    /**
     * Активен ли аккаунт
     */
    private boolean enabled;
    
    // Конструкторы
    public UserResponse() {
        // Пустой конструктор для Jackson (JSON сериализация)
    }
    
    public UserResponse(Long id, String username, String email, Role role, 
                       LocalDateTime createdAt, LocalDateTime updatedAt, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.enabled = enabled;
    }
    
    // Getters и Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", enabled=" + enabled +
                '}';
    }
} 