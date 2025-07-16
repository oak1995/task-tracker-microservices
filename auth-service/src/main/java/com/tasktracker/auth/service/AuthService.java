package com.tasktracker.auth.service;

import com.tasktracker.auth.dto.AuthResponse;
import com.tasktracker.auth.dto.LoginRequest;
import com.tasktracker.auth.dto.RegisterRequest;
import com.tasktracker.auth.dto.UserResponse;
import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.repository.UserRepository;
import com.tasktracker.auth.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис аутентификации и регистрации
 * 
 * Содержит бизнес-логику для:
 * - Регистрации новых пользователей
 * - Аутентификации пользователей
 * - Генерации JWT токенов
 * - Получения информации о пользователе
 */
@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    /**
     * Репозиторий для работы с пользователями
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Энкодер для шифрования паролей
     */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Сервис для работы с JWT токенами
     */
    @Autowired
    private JwtService jwtService;
    
    /**
     * Менеджер аутентификации Spring Security
     */
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /**
     * Регистрация нового пользователя
     * 
     * @param registerRequest данные для регистрации
     * @return AuthResponse с JWT токеном и информацией о пользователе
     * @throws RuntimeException если пользователь уже существует или данные некорректны
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        logger.info("Registering new user: {}", registerRequest.getUsername());
        
        // Проверяем, существует ли пользователь с таким именем
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            logger.error("Username already exists: {}", registerRequest.getUsername());
            throw new RuntimeException("Username already exists");
        }
        
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.error("Email already exists: {}", registerRequest.getEmail());
            throw new RuntimeException("Email already exists");
        }
        
        // Проверяем, совпадают ли пароли
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            logger.error("Passwords do not match for user: {}", registerRequest.getUsername());
            throw new RuntimeException("Passwords do not match");
        }
        
        // Создаем нового пользователя
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setEnabled(true);
        
        // Сохраняем пользователя в БД
        user = userRepository.save(user);
        
        // Генерируем JWT токен
        String jwt = jwtService.generateToken(user);
        
        logger.info("User registered successfully: {}", user.getUsername());
        
        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    
    /**
     * Аутентификация пользователя
     * 
     * @param loginRequest данные для входа
     * @return AuthResponse с JWT токеном и информацией о пользователе
     * @throws RuntimeException если данные некорректны
     */
    public AuthResponse authenticate(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            // Аутентификация через Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
                )
            );
            
            // Получаем аутентифицированного пользователя
            User user = (User) authentication.getPrincipal();
            
            // Генерируем JWT токен
            String jwt = jwtService.generateToken(user);
            
            logger.info("User authenticated successfully: {}", user.getUsername());
            
            return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
            
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            throw new RuntimeException("Invalid username or password");
        }
    }
    
    /**
     * Получение информации о пользователе по ID
     * 
     * @param userId ID пользователя
     * @return UserResponse с информацией о пользователе
     * @throws RuntimeException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        logger.debug("Getting user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new RuntimeException("User not found");
            });
        
        return mapToUserResponse(user);
    }
    
    /**
     * Получение информации о пользователе по имени пользователя
     * 
     * @param username имя пользователя
     * @return UserResponse с информацией о пользователе
     * @throws RuntimeException если пользователь не найден
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("User not found with username: {}", username);
                return new RuntimeException("User not found");
            });
        
        return mapToUserResponse(user);
    }
    
    /**
     * Проверка валидности JWT токена
     * 
     * @param token JWT токен
     * @return true если токен валиден
     */
    public boolean validateToken(String token) {
        try {
            return jwtService.isTokenValid(token);
        } catch (Exception e) {
            logger.error("Token validation failed", e);
            return false;
        }
    }
    
    /**
     * Извлечение имени пользователя из JWT токена
     * 
     * @param token JWT токен
     * @return имя пользователя
     */
    public String getUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }
    
    /**
     * Преобразование User в UserResponse
     * 
     * @param user пользователь
     * @return UserResponse
     */
    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.isEnabled()
        );
    }
} 