package com.tasktracker.auth.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * Перехватывает все HTTP запросы и проверяет наличие JWT токена
 * в заголовке Authorization
 * 
 * Наследует OncePerRequestFilter - гарантирует выполнение только один раз за запрос
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    /**
     * Название заголовка с JWT токеном
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Префикс Bearer для JWT токена
     */
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Сервис для работы с JWT токенами
     */
    @Autowired
    private JwtService jwtService;
    
    /**
     * Сервис для загрузки пользователей из БД
     */
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * Основной метод фильтра
     * 
     * Выполняется для каждого HTTP запроса
     * Проверяет JWT токен и устанавливает аутентификацию
     * 
     * @param request HTTP запрос
     * @param response HTTP ответ
     * @param filterChain цепочка фильтров
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Извлекаем JWT токен из заголовка Authorization
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Извлекаем имя пользователя из токена
                String username = jwtService.extractUsername(jwt);
                
                if (username != null) {
                    // Загружаем данные пользователя из БД
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // Проверяем валидность токена
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Создаем объект аутентификации
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        // Устанавливаем дополнительные детали запроса
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Сохраняем аутентификацию в SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.debug("User '{}' authenticated successfully", username);
                    } else {
                        logger.debug("JWT token is invalid for user '{}'", username);
                    }
                }
            }
        } catch (JwtException e) {
            logger.error("JWT authentication error: {}", e.getMessage());
            // Не выбрасываем исключение, просто продолжаем без аутентификации
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
        }
        
        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
    
    /**
     * Извлечение JWT токена из заголовка Authorization
     * 
     * Ожидаемый формат: "Bearer <jwt-token>"
     * 
     * @param request HTTP запрос
     * @return JWT токен или null если не найден
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
} 