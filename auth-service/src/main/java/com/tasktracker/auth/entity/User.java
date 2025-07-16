package com.tasktracker.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Сущность пользователя в системе
 * 
 * Реализует UserDetails для интеграции с Spring Security
 * Содержит всю необходимую информацию о пользователе
 * 
 * @Entity - помечает класс как JPA сущность
 * @Table - указывает имя таблицы в БД
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    
    /**
     * Уникальный идентификатор пользователя
     * 
     * @Id - помечает поле как первичный ключ
     * @GeneratedValue - автоматическая генерация значения
     * @Column - настройки столбца в БД
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * Имя пользователя (логин)
     * 
     * @NotBlank - поле не может быть пустым
     * @Size - ограничение на длину
     * @Column - unique = true означает уникальность в БД
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    /**
     * Email пользователя
     * 
     * @Email - валидация email формата
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    /**
     * Зашифрованный пароль
     * 
     * Никогда не храним пароли в открытом виде!
     * Используем BCrypt для шифрования
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * Роль пользователя
     * 
     * @Enumerated - указывает, что это enum
     * EnumType.STRING - сохраняет в БД как строку (USER/ADMIN)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER; // По умолчанию USER
    
    /**
     * Дата создания аккаунта
     * 
     * @Column(updatable = false) - поле не может быть изменено после создания
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Дата последнего обновления
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Активен ли аккаунт
     * 
     * Позволяет деактивировать пользователей без удаления
     */
    @Column(name = "enabled")
    private boolean enabled = true;
    
    // Конструкторы
    public User() {
        // Пустой конструктор для JPA
    }
    
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }
    
    // Методы жизненного цикла JPA
    
    /**
     * Вызывается перед сохранением в БД
     * Автоматически устанавливает дату создания
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Вызывается перед обновлением в БД
     * Автоматически обновляет дату изменения
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Реализация UserDetails для Spring Security
    
    /**
     * Возвращает роли пользователя для Spring Security
     * 
     * В Spring Security роли должны начинаться с "ROLE_"
     * Например: ROLE_USER, ROLE_ADMIN
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    /**
     * Возвращает пароль пользователя
     * Spring Security будет использовать его для проверки
     */
    @Override
    public String getPassword() {
        return password;
    }
    
    /**
     * Возвращает имя пользователя
     * Spring Security будет использовать его для аутентификации
     */
    @Override
    public String getUsername() {
        return username;
    }
    
    /**
     * Аккаунт не заблокирован
     * В нашем случае всегда true, но можно добавить логику блокировки
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * Аккаунт не заблокирован
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * Пароль не просрочен
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * Пользователь активен
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    // Getters и Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
} 