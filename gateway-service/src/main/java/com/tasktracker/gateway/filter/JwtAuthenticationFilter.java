package com.tasktracker.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Authentication Filter для Gateway
 * 
 * Этот фильтр проверяет JWT токены для защищенных endpoints
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.header}")
    private String jwtHeader;
    
    @Value("${jwt.prefix}")
    private String jwtPrefix;
    
    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractToken(exchange.getRequest().getHeaders());
            
            if (token == null) {
                logger.warn("Отсутствует JWT токен в запросе: {}", exchange.getRequest().getURI());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            try {
                Claims claims = validateToken(token);
                
                // Добавляем информацию о пользователе в заголовки для downstream сервисов
                exchange.getRequest().mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-User-Name", claims.get("username", String.class))
                        .header("X-User-Roles", claims.get("roles", String.class))
                        .build();
                
                logger.debug("JWT токен валиден для пользователя: {}", claims.getSubject());
                
                return chain.filter(exchange);
                
            } catch (Exception e) {
                logger.error("Ошибка валидации JWT токена: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
    
    /**
     * Извлекает JWT токен из заголовков запроса
     */
    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst(jwtHeader);
        
        if (authHeader != null && authHeader.startsWith(jwtPrefix)) {
            return authHeader.substring(jwtPrefix.length()).trim();
        }
        
        return null;
    }
    
    /**
     * Валидирует JWT токен
     */
    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Проверяет, не истек ли токен
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
    
    /**
     * Конфигурация фильтра
     */
    public static class Config {
        // Можно добавить настройки фильтра
    }
} 