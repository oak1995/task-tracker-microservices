package com.tasktracker.task.security;

import com.tasktracker.task.client.AuthServiceClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT фильтр для авторизации запросов
 * 
 * Этот фильтр перехватывает все HTTP запросы и проверяет наличие
 * и валидность JWT токена. При валидном токене устанавливает
 * контекст безопасности Spring Security.
 * 
 * Принципы работы:
 * 1. Извлекает JWT токен из заголовка Authorization
 * 2. Валидирует токен через Auth Service
 * 3. Устанавливает пользователя в SecurityContext
 * 4. Передает управление дальше по цепочке фильтров
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    /**
     * Основной метод фильтра
     * 
     * Вызывается для каждого HTTP запроса
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Извлекаем JWT токен из заголовка
            String jwt = extractJwtFromRequest(request);
            
            // Если токен есть и пользователь еще не аутентифицирован
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Валидируем токен через Auth Service
                AuthServiceClient.UserInfo userInfo = validateTokenWithAuthService(jwt);
                
                if (userInfo != null && userInfo.getEnabled()) {
                    // Создаем аутентификацию Spring Security
                    UsernamePasswordAuthenticationToken authentication = 
                            createAuthentication(userInfo, request);
                    
                    // Устанавливаем в SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Добавляем User ID в заголовок для контроллеров
                    request.setAttribute("userId", userInfo.getId());
                    
                    logger.debug("User {} authenticated successfully", userInfo.getUsername());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error during JWT authentication: {}", e.getMessage(), e);
            // Не блокируем запрос, позволяем Spring Security решить что делать
        }
        
        // Передаем управление дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
    
    /**
     * Извлекает JWT токен из заголовка Authorization
     * 
     * Ожидает формат: "Bearer <token>"
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * Валидирует токен через Auth Service
     * 
     * Отправляет запрос к Auth Service для проверки токена
     */
    private AuthServiceClient.UserInfo validateTokenWithAuthService(String jwt) {
        try {
            String bearerToken = BEARER_PREFIX + jwt;
            return authServiceClient.validateToken(bearerToken);
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Создает объект аутентификации Spring Security
     * 
     * Устанавливает пользователя, его роли и детали запроса
     */
    private UsernamePasswordAuthenticationToken createAuthentication(
            AuthServiceClient.UserInfo userInfo, 
            HttpServletRequest request) {
        
        // Создаем список ролей пользователя
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + userInfo.getRole())
        );
        
        // Создаем объект аутентификации
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                        userInfo.getUsername(), 
                        null, 
                        authorities
                );
        
        // Добавляем детали запроса (IP адрес, session ID и т.д.)
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        return authentication;
    }
    
    /**
     * Определяет, нужно ли применять фильтр к данному запросу
     * 
     * Пропускаем публичные endpoints (health check, swagger и т.д.)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Список публичных путей, которые не требуют аутентификации
        return path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/favicon.ico");
    }
} 