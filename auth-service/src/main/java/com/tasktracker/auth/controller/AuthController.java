package com.tasktracker.auth.controller;

import com.tasktracker.auth.dto.AuthResponse;
import com.tasktracker.auth.dto.LoginRequest;
import com.tasktracker.auth.dto.RegisterRequest;
import com.tasktracker.auth.dto.UserResponse;
import com.tasktracker.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для аутентификации и авторизации
 * 
 * Обрабатывает HTTP запросы для:
 * - Регистрации пользователей
 * - Аутентификации пользователей
 * - Получения профиля пользователя
 * - Валидации JWT токенов
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Операции аутентификации и авторизации")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    /**
     * Сервис аутентификации
     */
    @Autowired
    private AuthService authService;
    
    /**
     * Регистрация нового пользователя
     * 
     * @param registerRequest данные для регистрации
     * @return AuthResponse с JWT токеном
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создает новый аккаунт пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "409", description = "Пользователь уже существует")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registration request for user: {}", registerRequest.getUsername());
        
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            logger.info("User registered successfully: {}", registerRequest.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Registration failed", e.getMessage()));
        }
    }
    
    /**
     * Аутентификация пользователя
     * 
     * @param loginRequest данные для входа
     * @return AuthResponse с JWT токеном
     */
    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя", description = "Вход в систему по логину/email и паролю")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
        @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login request for user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            AuthResponse authResponse = authService.authenticate(loginRequest);
            logger.info("User authenticated successfully: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed", e.getMessage()));
        }
    }
    
    /**
     * Получение профиля текущего пользователя
     * 
     * @return UserResponse с информацией о пользователе
     */
    @GetMapping("/profile")
    @Operation(summary = "Получение профиля пользователя", description = "Возвращает информацию о текущем пользователе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль получен успешно"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> getProfile() {
        try {
            // Получаем текущего аутентифицированного пользователя
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            logger.debug("Getting profile for user: {}", username);
            
            UserResponse userResponse = authService.getUserByUsername(username);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            logger.error("Failed to get profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Profile not found", e.getMessage()));
        }
    }
    
    /**
     * Получение информации о пользователе по ID
     * 
     * @param userId ID пользователя
     * @return UserResponse с информацией о пользователе
     */
    @GetMapping("/users/{userId}")
    @Operation(summary = "Получение пользователя по ID", description = "Возвращает информацию о пользователе по его ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<?> getUserById(@Parameter(description = "ID пользователя") @PathVariable Long userId) {
        try {
            logger.debug("Getting user by ID: {}", userId);
            
            UserResponse userResponse = authService.getUserById(userId);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            logger.error("Failed to get user by ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User not found", e.getMessage()));
        }
    }
    
    /**
     * Валидация JWT токена
     * 
     * @param token JWT токен
     * @return статус валидности токена
     */
    @GetMapping("/validate")
    @Operation(summary = "Валидация JWT токена", description = "Проверяет валидность JWT токена")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус валидации"),
        @ApiResponse(responseCode = "400", description = "Токен не предоставлен")
    })
    public ResponseEntity<?> validateToken(@Parameter(description = "JWT токен") @RequestParam String token) {
        logger.debug("Validating token");
        
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Validation failed", "Token is required"));
        }
        
        try {
            boolean isValid = authService.validateToken(token);
            
            if (isValid) {
                String username = authService.getUsernameFromToken(token);
                return ResponseEntity.ok(new TokenValidationResponse(true, username, "Token is valid"));
            } else {
                return ResponseEntity.ok(new TokenValidationResponse(false, null, "Token is invalid"));
            }
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.ok(new TokenValidationResponse(false, null, "Token is invalid"));
        }
    }
    
    /**
     * Класс для ответа с ошибкой
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        // Getters
        public String getError() {
            return error;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Класс для ответа валидации токена
     */
    public static class TokenValidationResponse {
        private boolean valid;
        private String username;
        private String message;
        
        public TokenValidationResponse(boolean valid, String username, String message) {
            this.valid = valid;
            this.username = username;
            this.message = message;
        }
        
        // Getters
        public boolean isValid() {
            return valid;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 