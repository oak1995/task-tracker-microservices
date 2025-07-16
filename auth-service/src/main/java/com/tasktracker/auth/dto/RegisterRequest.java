package com.tasktracker.auth.dto;

import com.tasktracker.auth.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO для запроса регистрации нового пользователя
 * 
 * Используется в POST /auth/register
 * Содержит все необходимые поля для создания нового пользователя
 */
public class RegisterRequest {
    
    /**
     * Имя пользователя (логин)
     * 
     * Должно быть уникальным в системе
     * Может содержать буквы, цифры и подчеркивания
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    /**
     * Email пользователя
     * 
     * Должен быть уникальным в системе
     * Используется для уведомлений и восстановления пароля
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * Пароль пользователя
     * 
     * Передается в открытом виде, но по HTTPS
     * На сервере будет зашифрован с помощью BCrypt
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
    
    /**
     * Подтверждение пароля
     * 
     * Должно совпадать с основным паролем
     * Проверяется на уровне сервиса
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    
    /**
     * Роль пользователя
     * 
     * По умолчанию USER
     * Только администраторы могут создавать других администраторов
     */
    private Role role = Role.USER;
    
    // Конструкторы
    public RegisterRequest() {
        // Пустой конструктор для Jackson (JSON десериализация)
    }
    
    public RegisterRequest(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = Role.USER;
    }
    
    public RegisterRequest(String username, String email, String password, String confirmPassword, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }
    
    // Getters и Setters
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    // toString для логирования (БЕЗ ПАРОЛЕЙ!)
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", role=" + role +
                '}';
    }
} 