package com.tasktracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для запроса аутентификации (логина)
 * 
 * Используется в POST /auth/login
 * Содержит только необходимые поля для входа в систему
 */
public class LoginRequest {
    
    /**
     * Имя пользователя или email
     * 
     * Пользователь может войти как по username, так и по email
     * 
     * @NotBlank - поле не может быть пустым
     * @Size - ограничение на длину
     */
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 50, message = "Username or email must be between 3 and 50 characters")
    private String usernameOrEmail;
    
    /**
     * Пароль пользователя
     * 
     * Передается в открытом виде, но по HTTPS
     * На сервере сравнивается с зашифрованным паролем из БД
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
    
    // Конструкторы
    public LoginRequest() {
        // Пустой конструктор для Jackson (JSON десериализация)
    }
    
    public LoginRequest(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
    
    // Getters и Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // toString для логирования (БЕЗ ПАРОЛЯ!)
    @Override
    public String toString() {
        return "LoginRequest{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
} 