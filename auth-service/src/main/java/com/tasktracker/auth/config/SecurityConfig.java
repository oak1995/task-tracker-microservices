package com.tasktracker.auth.config;

import com.tasktracker.auth.security.JwtAuthenticationFilter;
import com.tasktracker.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Конфигурация Spring Security
 * 
 * Настраивает:
 * - JWT аутентификацию
 * - Авторизацию endpoints
 * - CORS
 * - Отключение session (stateless)
 * - Шифрование паролей
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    /**
     * Сервис для загрузки пользователей
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    /**
     * JWT фильтр аутентификации
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * CORS конфигурация отключена - управляется Gateway Service
     */
    // @Autowired
    // private CorsConfigurationSource corsConfigurationSource;
    
    /**
     * Энкодер паролей - BCrypt
     * 
     * BCrypt - это криптографическая хеш-функция, специально разработанная для паролей
     * Преимущества:
     * - Адаптивная (можно настроить сложность)
     * - Устойчивая к rainbow table атакам
     * - Включает salt автоматически
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Провайдер аутентификации
     * 
     * Связывает UserDetailsService с PasswordEncoder
     * Используется для проверки логина/пароля
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    /**
     * Менеджер аутентификации
     * 
     * Используется в AuthService для аутентификации пользователей
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Основная конфигурация безопасности
     * 
     * Настраивает цепочку фильтров безопасности
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Отключаем CSRF для REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS отключен - управляется Gateway Service
            .cors(cors -> cors.disable())
            
            // Настраиваем авторизацию
            .authorizeHttpRequests(authorize -> authorize
                // Публичные endpoints (не требуют аутентификации)
                .requestMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/validate",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/actuator/**"
                ).permitAll()
                
                // Все остальные endpoints требуют аутентификации
                .anyRequest().authenticated()
            )
            
            // Отключаем сессии (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Добавляем провайдер аутентификации
            .authenticationProvider(authenticationProvider())
            
            // Добавляем JWT фильтр перед стандартным фильтром аутентификации
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Конфигурация CORS отключена - управляется Gateway Service
     * 
     * Gateway Service является единственной точкой входа и управляет CORS политикой
     * для всех микросервисов
     */
    /*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Разрешенные домены (для production нужно указать конкретные домены)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Разрешенные HTTP методы
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Разрешенные заголовки
        configuration.setAllowedHeaders(List.of("*"));
        
        // Разрешить отправку cookies
        configuration.setAllowCredentials(true);
        
        // Применяем конфигурацию ко всем endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    */
} 