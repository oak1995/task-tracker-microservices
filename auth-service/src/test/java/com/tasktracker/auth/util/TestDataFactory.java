package com.tasktracker.auth.util;

import com.tasktracker.auth.dto.LoginRequest;
import com.tasktracker.auth.dto.RegisterRequest;
import com.tasktracker.auth.entity.Role;
import com.tasktracker.auth.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Фабрика для создания тестовых данных
 * 
 * Предоставляет методы для создания объектов User, DTO и других
 * сущностей для использования в тестах
 */
public class TestDataFactory {
    
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Создание тестового пользователя
     * 
     * @param username имя пользователя
     * @param email email
     * @param password пароль
     * @param role роль
     * @return User объект
     */
    public static User createUser(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }
    
    /**
     * Создание тестового пользователя с ID
     * 
     * @param id ID пользователя
     * @param username имя пользователя
     * @param email email
     * @param password пароль
     * @param role роль
     * @return User объект
     */
    public static User createUserWithId(Long id, String username, String email, String password, Role role) {
        User user = createUser(username, email, password, role);
        user.setId(id);
        return user;
    }
    
    /**
     * Создание обычного пользователя
     * 
     * @return User объект с ролью USER
     */
    public static User createRegularUser() {
        return createUser("testuser", "test@example.com", "password123", Role.USER);
    }
    
    /**
     * Создание администратора
     * 
     * @return User объект с ролью ADMIN
     */
    public static User createAdmin() {
        return createUser("admin", "admin@example.com", "admin123", Role.ADMIN);
    }
    
    /**
     * Создание запроса на регистрацию
     * 
     * @param username имя пользователя
     * @param email email
     * @param password пароль
     * @param confirmPassword подтверждение пароля
     * @return RegisterRequest объект
     */
    public static RegisterRequest createRegisterRequest(String username, String email, 
                                                      String password, String confirmPassword) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        request.setRole(Role.USER);
        return request;
    }
    
    /**
     * Создание валидного запроса на регистрацию
     * 
     * @return RegisterRequest объект
     */
    public static RegisterRequest createValidRegisterRequest() {
        return createRegisterRequest("newuser", "newuser@example.com", "Password123", "Password123");
    }
    
    /**
     * Создание запроса на логин
     * 
     * @param usernameOrEmail имя пользователя или email
     * @param password пароль
     * @return LoginRequest объект
     */
    public static LoginRequest createLoginRequest(String usernameOrEmail, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail(usernameOrEmail);
        request.setPassword(password);
        return request;
    }
    
    /**
     * Создание валидного запроса на логин
     * 
     * @return LoginRequest объект
     */
    public static LoginRequest createValidLoginRequest() {
        return createLoginRequest("testuser", "password123");
    }
    
    /**
     * Создание невалидного запроса на логин
     * 
     * @return LoginRequest объект с неверным паролем
     */
    public static LoginRequest createInvalidLoginRequest() {
        return createLoginRequest("testuser", "wrongpassword");
    }
    
    /**
     * Получение зашифрованного пароля
     * 
     * @param password открытый пароль
     * @return зашифрованный пароль
     */
    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
} 