package com.tasktracker.auth.repository;

import com.tasktracker.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 * 
 * Наследуется от JpaRepository, который предоставляет базовые CRUD операции:
 * - save(entity) - сохранить
 * - findById(id) - найти по ID
 * - findAll() - найти все
 * - delete(entity) - удалить
 * - existsById(id) - проверить существование
 * 
 * @Repository - помечает класс как компонент репозитория
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Найти пользователя по имени пользователя
     * 
     * Spring Data JPA автоматически создаст реализацию этого метода
     * на основе имени метода: findBy + Username
     * 
     * Генерируется SQL: SELECT * FROM users WHERE username = ?
     * 
     * @param username имя пользователя
     * @return Optional<User> - может быть пустым, если пользователь не найден
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Найти пользователя по email
     * 
     * Автоматически генерируется метод по имени
     * 
     * @param email email пользователя
     * @return Optional<User> - может быть пустым, если пользователь не найден
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Проверить, существует ли пользователь с таким именем
     * 
     * Автоматически генерируется метод по имени
     * existsBy + Username
     * 
     * @param username имя пользователя
     * @return true если пользователь существует, false иначе
     */
    boolean existsByUsername(String username);
    
    /**
     * Проверить, существует ли пользователь с таким email
     * 
     * @param email email пользователя
     * @return true если пользователь существует, false иначе
     */
    boolean existsByEmail(String email);
    
    /**
     * Найти пользователя по имени пользователя или email
     * 
     * Используем кастомный запрос с @Query аннотацией
     * Это полезно для логина - пользователь может ввести как username, так и email
     * 
     * @param username имя пользователя
     * @param email email пользователя
     * @return Optional<User> - может быть пустым, если пользователь не найден
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
    
    /**
     * Найти активных пользователей
     * 
     * Автоматически генерируется метод по имени
     * findBy + Enabled
     * 
     * @param enabled статус активности
     * @return список активных пользователей
     */
    java.util.List<User> findByEnabled(boolean enabled);
} 