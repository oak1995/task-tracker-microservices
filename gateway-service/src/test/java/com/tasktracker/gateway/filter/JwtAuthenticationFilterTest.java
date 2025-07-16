package com.tasktracker.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тесты для JwtAuthenticationFilter
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Mock
    private GatewayFilterChain filterChain;
    
    @Mock
    private ServerHttpResponse response;
    
    private final String jwtSecret = "test-secret-key-for-testing-purposes-only-do-not-use-in-production";
    private final String jwtHeader = "Authorization";
    private final String jwtPrefix = "Bearer ";
    
    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "jwtHeader", jwtHeader);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "jwtPrefix", jwtPrefix);
    }
    
    @Test
    void testValidJwtTokenShouldAllowRequest() {
        // Arrange
        String validToken = generateValidToken("user123", "testuser", "ROLE_USER");
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, jwtPrefix + validToken)
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
        
        // Проверяем, что заголовки пользователя были добавлены
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        assertEquals("user123", modifiedRequest.getHeaders().getFirst("X-User-Id"));
        assertEquals("testuser", modifiedRequest.getHeaders().getFirst("X-User-Name"));
        assertEquals("ROLE_USER", modifiedRequest.getHeaders().getFirst("X-User-Roles"));
    }
    
    @Test
    void testMissingJwtTokenShouldReturnUnauthorized() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testInvalidJwtTokenShouldReturnUnauthorized() {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, jwtPrefix + invalidToken)
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testExpiredJwtTokenShouldReturnUnauthorized() {
        // Arrange
        String expiredToken = generateExpiredToken("user123", "testuser", "ROLE_USER");
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, jwtPrefix + expiredToken)
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testJwtTokenWithoutBearerPrefixShouldReturnUnauthorized() {
        // Arrange
        String validToken = generateValidToken("user123", "testuser", "ROLE_USER");
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, validToken) // Без префикса "Bearer "
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testJwtTokenWithWrongSignatureShouldReturnUnauthorized() {
        // Arrange
        String tokenWithWrongSignature = generateTokenWithWrongSignature("user123", "testuser", "ROLE_USER");
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, jwtPrefix + tokenWithWrongSignature)
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testMultipleRolesInJwtTokenShouldBeHandledCorrectly() {
        // Arrange
        String validToken = generateValidToken("user123", "testuser", "ROLE_USER,ROLE_ADMIN");
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header(jwtHeader, jwtPrefix + validToken)
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(filterChain.filter(any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());
        
        // Act
        var filter = jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config());
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
        
        // Проверяем, что роли были корректно переданы
        ServerHttpRequest modifiedRequest = exchange.getRequest();
        assertEquals("ROLE_USER,ROLE_ADMIN", modifiedRequest.getHeaders().getFirst("X-User-Roles"));
    }
    
    /**
     * Генерирует валидный JWT токен для тестов
     */
    private String generateValidToken(String userId, String username, String roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 час
                .signWith(key)
                .compact();
    }
    
    /**
     * Генерирует истекший JWT токен для тестов
     */
    private String generateExpiredToken(String userId, String username, String roles) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 часа назад
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 час назад
                .signWith(key)
                .compact();
    }
    
    /**
     * Генерирует JWT токен с неправильной подписью для тестов
     */
    private String generateTokenWithWrongSignature(String userId, String username, String roles) {
        SecretKey wrongKey = Keys.hmacShaKeyFor("wrong-secret-key".getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(wrongKey)
                .compact();
    }
} 