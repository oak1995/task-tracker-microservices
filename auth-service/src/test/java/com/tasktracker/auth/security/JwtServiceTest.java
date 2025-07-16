package com.tasktracker.auth.security;

import com.tasktracker.auth.TestBase;
import com.tasktracker.auth.util.TestDataFactory;
import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.entity.Role;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-тесты для JwtService
 * 
 * Тестируют:
 * - Генерацию JWT токенов
 * - Валидацию токенов
 * - Извлечение данных из токенов
 * - Проверку срока действия
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JwtService Tests")
class JwtServiceTest extends TestBase {
    
    @Autowired
    private JwtService jwtService;
    
    private User testUser;
    private String validToken;
    
    /**
     * Настройка перед каждым тестом
     */
    @BeforeEach
    void setUpTest() {
        // Создаем тестового пользователя
        testUser = TestDataFactory.createRegularUser();
        
        // Генерируем валидный токен
        validToken = jwtService.generateToken(testUser);
    }
    
    /**
     * Тест генерации JWT токена
     */
    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        // Given
        User user = TestDataFactory.createRegularUser();
        
        // When
        String token = jwtService.generateToken(user);
        
        // Then
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.contains("."), "Token should contain dots (JWT format)");
        
        // Проверяем, что токен содержит 3 части (header.payload.signature)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
    }
    
    /**
     * Тест извлечения имени пользователя из токена
     */
    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Given
        String expectedUsername = testUser.getUsername();
        
        // When
        String extractedUsername = jwtService.extractUsername(validToken);
        
        // Then
        assertNotNull(extractedUsername, "Username should not be null");
        assertEquals(expectedUsername, extractedUsername, "Extracted username should match");
    }
    
    /**
     * Тест извлечения роли из токена
     */
    @Test
    @DisplayName("Should extract role from token")
    void shouldExtractRoleFromToken() {
        // Given
        String expectedRole = "ROLE_" + testUser.getRole().name();
        
        // When
        String extractedRole = jwtService.extractRole(validToken);
        
        // Then
        assertNotNull(extractedRole, "Role should not be null");
        assertEquals(expectedRole, extractedRole, "Extracted role should match");
    }
    
    /**
     * Тест извлечения даты истечения из токена
     */
    @Test
    @DisplayName("Should extract expiration date from token")
    void shouldExtractExpirationDateFromToken() {
        // Given
        Date now = new Date();
        
        // When
        Date expiration = jwtService.extractExpiration(validToken);
        
        // Then
        assertNotNull(expiration, "Expiration date should not be null");
        assertTrue(expiration.after(now), "Expiration date should be in the future");
    }
    
    /**
     * Тест валидации валидного токена
     */
    @Test
    @DisplayName("Should validate valid token")
    void shouldValidateValidToken() {
        // Given
        User user = testUser;
        String token = validToken;
        
        // When
        boolean isValid = jwtService.isTokenValid(token, user);
        
        // Then
        assertTrue(isValid, "Valid token should be validated successfully");
    }
    
    /**
     * Тест валидации токена без UserDetails
     */
    @Test
    @DisplayName("Should validate token without UserDetails")
    void shouldValidateTokenWithoutUserDetails() {
        // Given
        String token = validToken;
        
        // When
        boolean isValid = jwtService.isTokenValid(token);
        
        // Then
        assertTrue(isValid, "Valid token should be validated successfully");
    }
    
    /**
     * Тест валидации невалидного токена
     */
    @Test
    @DisplayName("Should reject invalid token")
    void shouldRejectInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtService.extractUsername(invalidToken);
        }, "Invalid token should throw JwtException");
    }
    
    /**
     * Тест валидации токена с неправильным пользователем
     */
    @Test
    @DisplayName("Should reject token for wrong user")
    void shouldRejectTokenForWrongUser() {
        // Given
        User wrongUser = TestDataFactory.createUser("wronguser", "wrong@example.com", "password", Role.USER);
        String token = validToken;
        
        // When
        boolean isValid = jwtService.isTokenValid(token, wrongUser);
        
        // Then
        assertFalse(isValid, "Token should not be valid for wrong user");
    }
    
    /**
     * Тест валидации пустого токена
     */
    @Test
    @DisplayName("Should reject empty token")
    void shouldRejectEmptyToken() {
        // Given
        String emptyToken = "";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUsername(emptyToken);
        }, "Empty token should throw IllegalArgumentException");
    }
    
    /**
     * Тест валидации null токена
     */
    @Test
    @DisplayName("Should reject null token")
    void shouldRejectNullToken() {
        // Given
        String nullToken = null;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.extractUsername(nullToken);
        }, "Null token should throw IllegalArgumentException");
    }
    
    /**
     * Тест получения времени жизни токена
     */
    @Test
    @DisplayName("Should return expiration time")
    void shouldReturnExpirationTime() {
        // When
        Long expirationTime = jwtService.getExpirationTime();
        
        // Then
        assertNotNull(expirationTime, "Expiration time should not be null");
        assertTrue(expirationTime > 0, "Expiration time should be positive");
        assertEquals(3600000L, expirationTime, "Expiration time should match config (1 hour)");
    }
    
    /**
     * Тест генерации токена с дополнительными claims
     */
    @Test
    @DisplayName("Should generate token with extra claims")
    void shouldGenerateTokenWithExtraClaims() {
        // Given
        User user = testUser;
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("custom_claim", "custom_value");
        
        // When
        String token = jwtService.generateToken(extraClaims, user.getUsername());
        
        // Then
        assertNotNull(token, "Token should not be null");
        assertTrue(jwtService.isTokenValid(token), "Token should be valid");
        
        // Проверяем, что можем извлечь имя пользователя
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(user.getUsername(), extractedUsername, "Username should match");
    }
    
    /**
     * Тест генерации токена для администратора
     */
    @Test
    @DisplayName("Should generate token for admin user")
    void shouldGenerateTokenForAdminUser() {
        // Given
        User adminUser = TestDataFactory.createAdmin();
        
        // When
        String token = jwtService.generateToken(adminUser);
        
        // Then
        assertNotNull(token, "Token should not be null");
        assertTrue(jwtService.isTokenValid(token), "Token should be valid");
        
        // Проверяем роль админа
        String extractedRole = jwtService.extractRole(token);
        assertEquals("ROLE_ADMIN", extractedRole, "Role should be ADMIN");
    }
} 