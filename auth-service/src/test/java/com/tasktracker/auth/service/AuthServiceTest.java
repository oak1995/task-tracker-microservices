package com.tasktracker.auth.service;

import com.tasktracker.auth.dto.AuthResponse;
import com.tasktracker.auth.dto.LoginRequest;
import com.tasktracker.auth.dto.RegisterRequest;
import com.tasktracker.auth.dto.UserResponse;
import com.tasktracker.auth.entity.Role;
import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.repository.UserRepository;
import com.tasktracker.auth.security.JwtService;
import com.tasktracker.auth.util.TestDataFactory;
import com.tasktracker.auth.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для AuthService
 * 
 * Тестируют:
 * - Регистрацию пользователей
 * - Аутентификацию пользователей
 * - Получение информации о пользователе
 * - Валидацию токенов
 * - Обработку ошибок
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AuthService Tests")
class AuthServiceTest extends TestBase {
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private AuthService authService;
    
    private User testUser;
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private String testToken;
    
    /**
     * Настройка перед каждым тестом
     */
    @BeforeEach
    void setUpAuthServiceTest() {
        testUser = TestDataFactory.createRegularUser();
        testUser.setId(1L);
        
        validRegisterRequest = TestDataFactory.createValidRegisterRequest();
        validLoginRequest = TestDataFactory.createValidLoginRequest();
        
        testToken = "test.jwt.token";
    }
    
    /**
     * Тест успешной регистрации
     */
    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(testToken);
        
        // When
        AuthResponse response = authService.register(validRegisterRequest);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(testToken, response.getToken(), "Token should match");
        assertEquals(testUser.getId(), response.getUserId(), "User ID should match");
        assertEquals(testUser.getUsername(), response.getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), response.getEmail(), "Email should match");
        assertEquals(testUser.getRole(), response.getRole(), "Role should match");
        
        // Verify interactions
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository).existsByEmail(validRegisterRequest.getEmail());
        verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }
    
    /**
     * Тест регистрации с существующим username
     */
    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(validRegisterRequest);
        });
        
        assertEquals("Username already exists", exception.getMessage());
        
        // Verify that we don't check email or save user
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }
    
    /**
     * Тест регистрации с существующим email
     */
    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(validRegisterRequest);
        });
        
        assertEquals("Email already exists", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).existsByUsername(validRegisterRequest.getUsername());
        verify(userRepository).existsByEmail(validRegisterRequest.getEmail());
        verify(userRepository, never()).save(any());
    }
    
    /**
     * Тест регистрации с несовпадающими паролями
     */
    @Test
    @DisplayName("Should throw exception when passwords do not match")
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        // Given
        validRegisterRequest.setConfirmPassword("differentPassword");
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(validRegisterRequest);
        });
        
        assertEquals("Passwords do not match", exception.getMessage());
        
        // Verify we don't save user
        verify(userRepository, never()).save(any());
    }
    
    /**
     * Тест успешной аутентификации
     */
    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn(testToken);
        
        // When
        AuthResponse response = authService.authenticate(validLoginRequest);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(testToken, response.getToken(), "Token should match");
        assertEquals(testUser.getId(), response.getUserId(), "User ID should match");
        assertEquals(testUser.getUsername(), response.getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), response.getEmail(), "Email should match");
        assertEquals(testUser.getRole(), response.getRole(), "Role should match");
        
        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
    }
    
    /**
     * Тест неуспешной аутентификации
     */
    @Test
    @DisplayName("Should throw exception on failed authentication")
    void shouldThrowExceptionOnFailedAuthentication() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(validLoginRequest);
        });
        
        assertEquals("Invalid username or password", exception.getMessage());
        
        // Verify we don't generate token
        verify(jwtService, never()).generateToken(any());
    }
    
    /**
     * Тест получения пользователя по ID
     */
    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        // When
        UserResponse response = authService.getUserById(userId);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(testUser.getId(), response.getId(), "ID should match");
        assertEquals(testUser.getUsername(), response.getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), response.getEmail(), "Email should match");
        assertEquals(testUser.getRole(), response.getRole(), "Role should match");
        
        // Verify interaction
        verify(userRepository).findById(userId);
    }
    
    /**
     * Тест получения несуществующего пользователя по ID
     */
    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getUserById(userId);
        });
        
        assertEquals("User not found", exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findById(userId);
    }
    
    /**
     * Тест получения пользователя по username
     */
    @Test
    @DisplayName("Should get user by username successfully")
    void shouldGetUserByUsernameSuccessfully() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        
        // When
        UserResponse response = authService.getUserByUsername(username);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(testUser.getId(), response.getId(), "ID should match");
        assertEquals(testUser.getUsername(), response.getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), response.getEmail(), "Email should match");
        assertEquals(testUser.getRole(), response.getRole(), "Role should match");
        
        // Verify interaction
        verify(userRepository).findByUsername(username);
    }
    
    /**
     * Тест получения несуществующего пользователя по username
     */
    @Test
    @DisplayName("Should throw exception when user not found by username")
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getUserByUsername(username);
        });
        
        assertEquals("User not found", exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsername(username);
    }
    
    /**
     * Тест валидации валидного токена
     */
    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Given
        when(jwtService.isTokenValid(testToken)).thenReturn(true);
        
        // When
        boolean isValid = authService.validateToken(testToken);
        
        // Then
        assertTrue(isValid, "Token should be valid");
        
        // Verify interaction
        verify(jwtService).isTokenValid(testToken);
    }
    
    /**
     * Тест валидации невалидного токена
     */
    @Test
    @DisplayName("Should validate invalid token")
    void shouldValidateInvalidToken() {
        // Given
        when(jwtService.isTokenValid(testToken)).thenReturn(false);
        
        // When
        boolean isValid = authService.validateToken(testToken);
        
        // Then
        assertFalse(isValid, "Token should be invalid");
        
        // Verify interaction
        verify(jwtService).isTokenValid(testToken);
    }
    
    /**
     * Тест валидации токена с исключением
     */
    @Test
    @DisplayName("Should handle token validation exception")
    void shouldHandleTokenValidationException() {
        // Given
        when(jwtService.isTokenValid(testToken)).thenThrow(new RuntimeException("Token error"));
        
        // When
        boolean isValid = authService.validateToken(testToken);
        
        // Then
        assertFalse(isValid, "Token should be invalid on exception");
        
        // Verify interaction
        verify(jwtService).isTokenValid(testToken);
    }
    
    /**
     * Тест извлечения имени пользователя из токена
     */
    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Given
        String expectedUsername = "testuser";
        when(jwtService.extractUsername(testToken)).thenReturn(expectedUsername);
        
        // When
        String username = authService.getUsernameFromToken(testToken);
        
        // Then
        assertEquals(expectedUsername, username, "Username should match");
        
        // Verify interaction
        verify(jwtService).extractUsername(testToken);
    }
    
    /**
     * Тест регистрации администратора
     */
    @Test
    @DisplayName("Should register admin user successfully")
    void shouldRegisterAdminUserSuccessfully() {
        // Given
        validRegisterRequest.setRole(Role.ADMIN);
        User adminUser = TestDataFactory.createAdmin();
        adminUser.setId(1L);
        
        when(userRepository.existsByUsername(validRegisterRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(testToken);
        
        // When
        AuthResponse response = authService.register(validRegisterRequest);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(Role.ADMIN, response.getRole(), "Role should be ADMIN");
        assertEquals(testToken, response.getToken(), "Token should match");
        
        // Verify interactions
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }
} 