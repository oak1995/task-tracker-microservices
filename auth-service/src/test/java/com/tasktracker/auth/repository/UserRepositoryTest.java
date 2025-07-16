package com.tasktracker.auth.repository;

import com.tasktracker.auth.entity.Role;
import com.tasktracker.auth.entity.User;
import com.tasktracker.auth.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для UserRepository
 * 
 * Тестируют:
 * - Базовые CRUD операции
 * - Кастомные методы поиска
 * - Проверки существования
 * - Работу с H2 базой данных
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User testUser;
    private User adminUser;
    
    /**
     * Настройка перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createRegularUser();
        adminUser = TestDataFactory.createAdmin();
        
        // Сохраняем пользователей в БД
        entityManager.persist(testUser);
        entityManager.persist(adminUser);
        entityManager.flush();
    }
    
    /**
     * Тест сохранения пользователя
     */
    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        User newUser = TestDataFactory.createUser("newuser", "newuser@example.com", "password123", Role.USER);
        
        // When
        User savedUser = userRepository.save(newUser);
        
        // Then
        assertNotNull(savedUser.getId(), "ID should be generated");
        assertEquals(newUser.getUsername(), savedUser.getUsername(), "Username should match");
        assertEquals(newUser.getEmail(), savedUser.getEmail(), "Email should match");
        assertEquals(newUser.getRole(), savedUser.getRole(), "Role should match");
        assertTrue(savedUser.isEnabled(), "User should be enabled");
        assertNotNull(savedUser.getCreatedAt(), "Created date should be set");
        assertNotNull(savedUser.getUpdatedAt(), "Updated date should be set");
    }
    
    /**
     * Тест поиска пользователя по ID
     */
    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // Given
        Long userId = testUser.getId();
        
        // When
        Optional<User> foundUser = userRepository.findById(userId);
        
        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email should match");
    }
    
    /**
     * Тест поиска пользователя по username
     */
    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        String username = testUser.getUsername();
        
        // When
        Optional<User> foundUser = userRepository.findByUsername(username);
        
        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email should match");
    }
    
    /**
     * Тест поиска пользователя по email
     */
    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        String email = testUser.getEmail();
        
        // When
        Optional<User> foundUser = userRepository.findByEmail(email);
        
        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email should match");
    }
    
    /**
     * Тест поиска пользователя по username или email (по username)
     */
    @Test
    @DisplayName("Should find user by username or email using username")
    void shouldFindUserByUsernameOrEmailUsingUsername() {
        // Given
        String username = testUser.getUsername();
        
        // When
        Optional<User> foundUser = userRepository.findByUsernameOrEmail(username);
        
        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email should match");
    }
    
    /**
     * Тест поиска пользователя по username или email (по email)
     */
    @Test
    @DisplayName("Should find user by username or email using email")
    void shouldFindUserByUsernameOrEmailUsingEmail() {
        // Given
        String email = testUser.getEmail();
        
        // When
        Optional<User> foundUser = userRepository.findByUsernameOrEmail(email);
        
        // Then
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals(testUser.getUsername(), foundUser.get().getUsername(), "Username should match");
        assertEquals(testUser.getEmail(), foundUser.get().getEmail(), "Email should match");
    }
    
    /**
     * Тест проверки существования пользователя по username
     */
    @Test
    @DisplayName("Should check if user exists by username")
    void shouldCheckIfUserExistsByUsername() {
        // Given
        String existingUsername = testUser.getUsername();
        String nonExistingUsername = "nonexistent";
        
        // When
        boolean exists = userRepository.existsByUsername(existingUsername);
        boolean notExists = userRepository.existsByUsername(nonExistingUsername);
        
        // Then
        assertTrue(exists, "User should exist");
        assertFalse(notExists, "User should not exist");
    }
    
    /**
     * Тест проверки существования пользователя по email
     */
    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Given
        String existingEmail = testUser.getEmail();
        String nonExistingEmail = "nonexistent@example.com";
        
        // When
        boolean exists = userRepository.existsByEmail(existingEmail);
        boolean notExists = userRepository.existsByEmail(nonExistingEmail);
        
        // Then
        assertTrue(exists, "User should exist");
        assertFalse(notExists, "User should not exist");
    }
    
    /**
     * Тест поиска активных пользователей
     */
    @Test
    @DisplayName("Should find enabled users")
    void shouldFindEnabledUsers() {
        // Given
        User disabledUser = TestDataFactory.createUser("disabled", "disabled@example.com", "password", Role.USER);
        disabledUser.setEnabled(false);
        entityManager.persist(disabledUser);
        entityManager.flush();
        
        // When
        List<User> enabledUsers = userRepository.findByEnabled(true);
        List<User> disabledUsers = userRepository.findByEnabled(false);
        
        // Then
        assertEquals(2, enabledUsers.size(), "Should find 2 enabled users");
        assertEquals(1, disabledUsers.size(), "Should find 1 disabled user");
        
        // Проверяем, что все найденные пользователи действительно активны
        assertTrue(enabledUsers.stream().allMatch(User::isEnabled), "All users should be enabled");
        assertTrue(disabledUsers.stream().noneMatch(User::isEnabled), "All users should be disabled");
    }
    
    /**
     * Тест поиска всех пользователей
     */
    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // When
        List<User> allUsers = userRepository.findAll();
        
        // Then
        assertEquals(2, allUsers.size(), "Should find 2 users");
        
        // Проверяем, что оба пользователя найдены
        assertTrue(allUsers.stream().anyMatch(user -> user.getUsername().equals(testUser.getUsername())), 
                "Should contain test user");
        assertTrue(allUsers.stream().anyMatch(user -> user.getUsername().equals(adminUser.getUsername())), 
                "Should contain admin user");
    }
    
    /**
     * Тест обновления пользователя
     */
    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        String newEmail = "updated@example.com";
        testUser.setEmail(newEmail);
        
        // When
        User updatedUser = userRepository.save(testUser);
        entityManager.flush();
        
        // Then
        assertEquals(newEmail, updatedUser.getEmail(), "Email should be updated");
        assertNotNull(updatedUser.getUpdatedAt(), "Updated date should be set");
    }
    
    /**
     * Тест удаления пользователя
     */
    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        Long userId = testUser.getId();
        
        // When
        userRepository.deleteById(userId);
        entityManager.flush();
        
        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent(), "User should be deleted");
    }
    
    /**
     * Тест поиска несуществующего пользователя
     */
    @Test
    @DisplayName("Should return empty optional for non-existent user")
    void shouldReturnEmptyOptionalForNonExistentUser() {
        // Given
        String nonExistentUsername = "nonexistent";
        String nonExistentEmail = "nonexistent@example.com";
        
        // When
        Optional<User> userByUsername = userRepository.findByUsername(nonExistentUsername);
        Optional<User> userByEmail = userRepository.findByEmail(nonExistentEmail);
        Optional<User> userByUsernameOrEmail = userRepository.findByUsernameOrEmail(nonExistentUsername);
        
        // Then
        assertFalse(userByUsername.isPresent(), "Should not find user by username");
        assertFalse(userByEmail.isPresent(), "Should not find user by email");
        assertFalse(userByUsernameOrEmail.isPresent(), "Should not find user by username or email");
    }
    
    /**
     * Тест уникальности username
     */
    @Test
    @DisplayName("Should enforce username uniqueness")
    void shouldEnforceUsernameUniqueness() {
        // Given
        User duplicateUser = TestDataFactory.createUser(testUser.getUsername(), "different@example.com", "password", Role.USER);
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }, "Should throw exception for duplicate username");
    }
    
    /**
     * Тест уникальности email
     */
    @Test
    @DisplayName("Should enforce email uniqueness")
    void shouldEnforceEmailUniqueness() {
        // Given
        User duplicateUser = TestDataFactory.createUser("different", testUser.getEmail(), "password", Role.USER);
        
        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        }, "Should throw exception for duplicate email");
    }
    
    /**
     * Тест сортировки пользователей
     */
    @Test
    @DisplayName("Should sort users by username")
    void shouldSortUsersByUsername() {
        // Given
        User userA = TestDataFactory.createUser("aaa", "aaa@example.com", "password", Role.USER);
        User userZ = TestDataFactory.createUser("zzz", "zzz@example.com", "password", Role.USER);
        entityManager.persist(userA);
        entityManager.persist(userZ);
        entityManager.flush();
        
        // When
        List<User> users = userRepository.findAll();
        users.sort((u1, u2) -> u1.getUsername().compareTo(u2.getUsername()));
        
        // Then
        assertEquals("aaa", users.get(0).getUsername(), "First user should be 'aaa'");
        assertEquals("zzz", users.get(users.size() - 1).getUsername(), "Last user should be 'zzz'");
    }
    
    /**
     * Тест подсчета пользователей
     */
    @Test
    @DisplayName("Should count users correctly")
    void shouldCountUsersCorrectly() {
        // When
        long count = userRepository.count();
        
        // Then
        assertEquals(2, count, "Should count 2 users");
    }
} 