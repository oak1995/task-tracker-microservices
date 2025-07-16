package com.tasktracker.auth.service;

import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.repository.UserRepository;
import com.tasktracker.auth.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для UserDetailsServiceImpl
 * 
 * Тестируют:
 * - Загрузку пользователя по username
 * - Загрузку пользователя по email
 * - Обработку ошибок при отсутствии пользователя
 * - Обработку деактивированных пользователей
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    
    private User testUser;
    
    /**
     * Настройка перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createRegularUser();
        testUser.setId(1L);
    }
    
    /**
     * Тест успешной загрузки пользователя по username
     */
    @Test
    @DisplayName("Should load user by username successfully")
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(testUser.getUsername(), userDetails.getUsername(), "Username should match");
        assertEquals(testUser.getPassword(), userDetails.getPassword(), "Password should match");
        assertTrue(userDetails.isEnabled(), "User should be enabled");
        assertTrue(userDetails.isAccountNonExpired(), "Account should not be expired");
        assertTrue(userDetails.isAccountNonLocked(), "Account should not be locked");
        assertTrue(userDetails.isCredentialsNonExpired(), "Credentials should not be expired");
        
        // Проверяем роли
        assertEquals(1, userDetails.getAuthorities().size(), "Should have one authority");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")), 
                "Should have ROLE_USER authority");
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест успешной загрузки пользователя по email
     */
    @Test
    @DisplayName("Should load user by email successfully")
    void shouldLoadUserByEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByUsernameOrEmail(email)).thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        
        // Then
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(testUser.getUsername(), userDetails.getUsername(), "Username should match");
        assertEquals(testUser.getPassword(), userDetails.getPassword(), "Password should match");
        assertTrue(userDetails.isEnabled(), "User should be enabled");
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(email);
    }
    
    /**
     * Тест загрузки администратора
     */
    @Test
    @DisplayName("Should load admin user successfully")
    void shouldLoadAdminUserSuccessfully() {
        // Given
        User adminUser = TestDataFactory.createAdmin();
        adminUser.setId(1L);
        String username = "admin";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(adminUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(adminUser.getUsername(), userDetails.getUsername(), "Username should match");
        
        // Проверяем роль админа
        assertEquals(1, userDetails.getAuthorities().size(), "Should have one authority");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")), 
                "Should have ROLE_ADMIN authority");
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест обработки несуществующего пользователя
     */
    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.empty());
        
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        assertEquals("User not found: " + username, exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест обработки деактивированного пользователя
     */
    @Test
    @DisplayName("Should throw exception when user is disabled")
    void shouldThrowExceptionWhenUserIsDisabled() {
        // Given
        testUser.setEnabled(false);
        String username = "testuser";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(testUser));
        
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        assertEquals("User account is disabled: " + username, exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест загрузки пользователя с null username
     */
    @Test
    @DisplayName("Should handle null username")
    void shouldHandleNullUsername() {
        // Given
        String username = null;
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.empty());
        
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        assertEquals("User not found: " + username, exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест загрузки пользователя с пустым username
     */
    @Test
    @DisplayName("Should handle empty username")
    void shouldHandleEmptyUsername() {
        // Given
        String username = "";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.empty());
        
        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        assertEquals("User not found: " + username, exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест загрузки пользователя с пробелами в username
     */
    @Test
    @DisplayName("Should handle username with spaces")
    void shouldHandleUsernameWithSpaces() {
        // Given
        String username = "  testuser  ";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals(testUser.getUsername(), userDetails.getUsername(), "Username should match");
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест корректного преобразования ролей
     */
    @Test
    @DisplayName("Should convert roles correctly")
    void shouldConvertRolesCorrectly() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsernameOrEmail(username)).thenReturn(Optional.of(testUser));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertNotNull(userDetails.getAuthorities(), "Authorities should not be null");
        assertFalse(userDetails.getAuthorities().isEmpty(), "Authorities should not be empty");
        
        // Проверяем, что роль правильно преобразована
        String expectedAuthority = "ROLE_" + testUser.getRole().name();
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(expectedAuthority)), 
                "Should have correct authority: " + expectedAuthority);
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
    
    /**
     * Тест обработки исключения от репозитория
     */
    @Test
    @DisplayName("Should handle repository exception")
    void shouldHandleRepositoryException() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsernameOrEmail(username))
                .thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        
        assertEquals("Database error", exception.getMessage());
        
        // Verify interaction
        verify(userRepository).findByUsernameOrEmail(username);
    }
} 