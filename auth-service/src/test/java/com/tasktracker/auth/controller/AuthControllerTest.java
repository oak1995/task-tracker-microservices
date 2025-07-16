package com.tasktracker.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasktracker.auth.dto.AuthResponse;
import com.tasktracker.auth.dto.LoginRequest;
import com.tasktracker.auth.dto.RegisterRequest;
import com.tasktracker.auth.dto.UserResponse;
import com.tasktracker.auth.entity.Role;
import com.tasktracker.auth.service.AuthService;
import com.tasktracker.auth.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit-тесты для AuthController
 * 
 * Тестируют:
 * - REST API endpoints
 * - Валидацию входных данных
 * - Обработку ошибок
 * - Авторизацию endpoints
 * - JSON сериализацию/десериализацию
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private AuthResponse authResponse;
    private UserResponse userResponse;
    
    /**
     * Настройка перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        validRegisterRequest = TestDataFactory.createValidRegisterRequest();
        validLoginRequest = TestDataFactory.createValidLoginRequest();
        
        authResponse = new AuthResponse(
            "jwt.token.here",
            1L,
            "testuser",
            "test@example.com",
            Role.USER
        );
        
        userResponse = new UserResponse(
            1L,
            "testuser",
            "test@example.com",
            Role.USER,
            LocalDateTime.now(),
            LocalDateTime.now(),
            true
        );
    }
    
    /**
     * Тест успешной регистрации
     */
    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);
        
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    /**
     * Тест регистрации с невалидными данными
     */
    @Test
    @DisplayName("Should return bad request for invalid registration data")
    void shouldReturnBadRequestForInvalidRegistrationData() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername(""); // Пустое имя пользователя
        invalidRequest.setEmail("invalid-email"); // Невалидный email
        invalidRequest.setPassword("123"); // Слишком короткий пароль
        invalidRequest.setConfirmPassword("123");
        
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Тест регистрации с существующим пользователем
     */
    @Test
    @DisplayName("Should return conflict when user already exists")
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        // Given
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already exists"));
        
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Registration failed"))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }
    
    /**
     * Тест успешного логина
     */
    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
        // Given
        when(authService.authenticate(any(LoginRequest.class))).thenReturn(authResponse);
        
        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    /**
     * Тест логина с неверными данными
     */
    @Test
    @DisplayName("Should return unauthorized for invalid login")
    void shouldReturnUnauthorizedForInvalidLogin() throws Exception {
        // Given
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Invalid username or password"));
        
        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication failed"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
    
    /**
     * Тест логина с невалидными данными
     */
    @Test
    @DisplayName("Should return bad request for invalid login data")
    void shouldReturnBadRequestForInvalidLoginData() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsernameOrEmail(""); // Пустое поле
        invalidRequest.setPassword(""); // Пустой пароль
        
        // When & Then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Тест получения профиля аутентифицированного пользователя
     */
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("Should get user profile successfully")
    void shouldGetUserProfileSuccessfully() throws Exception {
        // Given
        when(authService.getUserByUsername("testuser")).thenReturn(userResponse);
        
        // When & Then
        mockMvc.perform(get("/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true));
    }
    
    /**
     * Тест получения профиля без аутентификации
     */
    @Test
    @DisplayName("Should return unauthorized for profile without authentication")
    void shouldReturnUnauthorizedForProfileWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/profile"))
                .andExpect(status().isUnauthorized());
    }
    
    /**
     * Тест получения профиля несуществующего пользователя
     */
    @Test
    @WithMockUser(username = "nonexistent", roles = "USER")
    @DisplayName("Should return not found for non-existent user profile")
    void shouldReturnNotFoundForNonExistentUserProfile() throws Exception {
        // Given
        when(authService.getUserByUsername("nonexistent"))
                .thenThrow(new RuntimeException("User not found"));
        
        // When & Then
        mockMvc.perform(get("/auth/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Profile not found"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
    
    /**
     * Тест получения пользователя по ID
     */
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        // Given
        when(authService.getUserById(1L)).thenReturn(userResponse);
        
        // When & Then
        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
    
    /**
     * Тест получения несуществующего пользователя по ID
     */
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("Should return not found for non-existent user by ID")
    void shouldReturnNotFoundForNonExistentUserById() throws Exception {
        // Given
        when(authService.getUserById(999L))
                .thenThrow(new RuntimeException("User not found"));
        
        // When & Then
        mockMvc.perform(get("/auth/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
    
    /**
     * Тест валидации валидного токена
     */
    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() throws Exception {
        // Given
        String token = "valid.jwt.token";
        when(authService.validateToken(token)).thenReturn(true);
        when(authService.getUsernameFromToken(token)).thenReturn("testuser");
        
        // When & Then
        mockMvc.perform(get("/auth/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.message").value("Token is valid"));
    }
    
    /**
     * Тест валидации невалидного токена
     */
    @Test
    @DisplayName("Should validate invalid token")
    void shouldValidateInvalidToken() throws Exception {
        // Given
        String token = "invalid.jwt.token";
        when(authService.validateToken(token)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(get("/auth/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.message").value("Token is invalid"));
    }
    
    /**
     * Тест валидации токена с исключением
     */
    @Test
    @DisplayName("Should handle token validation exception")
    void shouldHandleTokenValidationException() throws Exception {
        // Given
        String token = "problematic.jwt.token";
        when(authService.validateToken(token)).thenThrow(new RuntimeException("Token error"));
        
        // When & Then
        mockMvc.perform(get("/auth/validate")
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.message").value("Token is invalid"));
    }
    
    /**
     * Тест валидации пустого токена
     */
    @Test
    @DisplayName("Should return bad request for empty token")
    void shouldReturnBadRequestForEmptyToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/validate")
                .param("token", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value("Token is required"));
    }
    
    /**
     * Тест валидации без токена
     */
    @Test
    @DisplayName("Should return bad request when token is missing")
    void shouldReturnBadRequestWhenTokenIsMissing() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/validate"))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Тест регистрации с паролем, не содержащим требуемые символы
     */
    @Test
    @DisplayName("Should return bad request for password without required characters")
    void shouldReturnBadRequestForPasswordWithoutRequiredCharacters() throws Exception {
        // Given
        RegisterRequest weakPasswordRequest = TestDataFactory.createValidRegisterRequest();
        weakPasswordRequest.setPassword("weakpassword"); // Нет цифр и заглавных букв
        weakPasswordRequest.setConfirmPassword("weakpassword");
        
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Тест регистрации с невалидным username
     */
    @Test
    @DisplayName("Should return bad request for invalid username")
    void shouldReturnBadRequestForInvalidUsername() throws Exception {
        // Given
        RegisterRequest invalidUsernameRequest = TestDataFactory.createValidRegisterRequest();
        invalidUsernameRequest.setUsername("invalid@username"); // Недопустимые символы
        
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUsernameRequest)))
                .andExpect(status().isBadRequest());
    }
    
    /**
     * Тест получения пользователя по ID без аутентификации
     */
    @Test
    @DisplayName("Should return unauthorized for getting user by ID without authentication")
    void shouldReturnUnauthorizedForGettingUserByIdWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/users/1"))
                .andExpect(status().isUnauthorized());
    }
    
    /**
     * Тест Content-Type для JSON запросов
     */
    @Test
    @DisplayName("Should require JSON content type for POST requests")
    void shouldRequireJsonContentTypeForPostRequests() throws Exception {
        // When & Then
        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(status().isUnsupportedMediaType());
    }
} 