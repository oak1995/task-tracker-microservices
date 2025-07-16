package com.tasktracker.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT токенами
 * 
 * Отвечает за:
 * - Генерацию JWT токенов
 * - Валидацию токенов
 * - Извлечение данных из токенов
 * - Проверку срока действия
 */
@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    /**
     * Секретный ключ для подписи JWT токенов
     * 
     * Загружается из application.yml
     * Должен быть достаточно длинным для безопасности
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * Время жизни JWT токена в миллисекундах
     * 
     * Загружается из application.yml
     * По умолчанию 24 часа
     */
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * Генерация JWT токена для пользователя
     * 
     * @param userDetails данные пользователя
     * @return JWT токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Добавляем роль пользователя в claims
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return generateToken(claims, userDetails.getUsername());
    }
    
    /**
     * Генерация JWT токена с дополнительными claims
     * 
     * @param extraClaims дополнительные данные для токена
     * @param username имя пользователя
     * @return JWT токен
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Извлечение имени пользователя из JWT токена
     * 
     * @param token JWT токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Извлечение даты истечения срока действия токена
     * 
     * @param token JWT токен
     * @return дата истечения
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Извлечение роли пользователя из JWT токена
     * 
     * @param token JWT токен
     * @return роль пользователя
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    /**
     * Извлечение конкретного claim из JWT токена
     * 
     * @param token JWT токен
     * @param claimsResolver функция для извлечения claim
     * @return значение claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Извлечение всех claims из JWT токена
     * 
     * @param token JWT токен
     * @return все claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("Failed to extract claims from token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token");
        }
    }
    
    /**
     * Проверка валидности JWT токена
     * 
     * @param token JWT токен
     * @param userDetails данные пользователя
     * @return true если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Проверка истечения срока действия токена
     * 
     * @param token JWT токен
     * @return true если токен истек
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Получение секретного ключа для подписи
     * 
     * @return секретный ключ
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Валидация JWT токена без проверки пользователя
     * 
     * Используется для первичной проверки токена
     * 
     * @param token JWT токен
     * @return true если токен валиден
     */
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Получение времени жизни токена в миллисекундах
     * 
     * @return время жизни токена
     */
    public Long getExpirationTime() {
        return expiration;
    }
} 