package com.tasktracker.task.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Конфигурация Spring Security для Task Service
 * 
 * Этот класс настраивает безопасность микросервиса:
 * - Аутентификация через JWT токены
 * - Авторизация доступа к endpoints
 * - CORS настройки для фронтенда
 * - Stateless сессии (без сохранения состояния)
 * 
 * Принципы безопасности микросервисов:
 * 1. Stateless - каждый запрос должен содержать всю информацию
 * 2. JWT токены - для передачи информации о пользователе
 * 3. Централизованная аутентификация - через Auth Service
 * 4. Минимальные права доступа - только необходимые
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * Основная конфигурация безопасности
     * 
     * Определяет какие endpoints требуют аутентификации,
     * какие роли имеют доступ к различным операциям.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http
            // Отключаем CSRF для stateless API
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS отключен - управляется Gateway Service
            .cors(cors -> cors.disable())
            
            // Настраиваем авторизацию запросов
            .authorizeHttpRequests(authz -> authz
                // Публичные endpoints - не требуют аутентификации
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                
                // Статистика - доступна всем аутентифицированным пользователям
                .requestMatchers(HttpMethod.GET, "/tasks/statistics").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/tasks/overdue").hasRole("USER")
                
                // Поиск задач - доступен всем пользователям
                .requestMatchers(HttpMethod.GET, "/tasks/search").hasRole("USER")
                
                // Просмотр задач - доступен всем пользователям
                .requestMatchers(HttpMethod.GET, "/tasks/**").hasRole("USER")
                
                // Создание задач - доступно всем пользователям
                .requestMatchers(HttpMethod.POST, "/tasks").hasRole("USER")
                
                // Обновление задач - доступно всем пользователям
                .requestMatchers(HttpMethod.PUT, "/tasks/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/tasks/**").hasRole("USER")
                
                // Назначение задач - доступно всем пользователям
                .requestMatchers(HttpMethod.POST, "/tasks/*/assign/**").hasRole("USER")
                
                // Удаление задач - доступно всем пользователям (проверка прав в сервисе)
                .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("USER")
                
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            
            // Настраиваем stateless сессии
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Добавляем JWT фильтр перед стандартным фильтром аутентификации
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    // @Autowired - отключено, CORS управляется Gateway Service
    // private CorsConfigurationSource corsConfigurationSource;
} 