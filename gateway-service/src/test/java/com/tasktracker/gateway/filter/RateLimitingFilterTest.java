package com.tasktracker.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для RateLimitingFilter
 */
@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;
    
    @Mock
    private ReactiveStringRedisTemplate redisTemplate;
    
    @Mock
    private ReactiveValueOperations<String, String> valueOperations;
    
    @Mock
    private GatewayFilterChain filterChain;
    
    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
        ReflectionTestUtils.setField(rateLimitingFilter, "redisTemplate", redisTemplate);
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }
    
    @Test
    void testAllowRequestWithinLimit() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response - первый запрос в окне
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain).filter(any(ServerWebExchange.class));
        assertNotEquals(HttpStatus.TOO_MANY_REQUESTS, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testBlockRequestWhenLimitExceeded() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response - превышение лимита
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(15L)); // Больше лимита
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        verify(filterChain, never()).filter(any(ServerWebExchange.class));
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange.getResponse().getStatusCode());
        
        // Проверяем заголовки
        assertEquals("10", exchange.getResponse().getHeaders().getFirst("X-RateLimit-Limit"));
        assertEquals("0", exchange.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"));
        assertEquals("1", exchange.getResponse().getHeaders().getFirst("Retry-After"));
    }
    
    @Test
    void testUseUserIdWhenAvailable() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .header("X-User-Id", "user123")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        // Проверяем, что Redis был вызван с ключом для пользователя
        verify(valueOperations).increment(startsWith("rate_limit:user:user123"));
    }
    
    @Test
    void testUseIpWhenUserIdNotAvailable() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("192.168.1.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        // Проверяем, что Redis был вызван с ключом для IP
        verify(valueOperations).increment(startsWith("rate_limit:ip:192.168.1.1"));
    }
    
    @Test
    void testAllowRequestOnRedisError() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis error
        when(valueOperations.increment(anyString())).thenReturn(Mono.error(new RuntimeException("Redis error")));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        // При ошибке Redis запрос должен быть разрешен
        verify(filterChain).filter(any(ServerWebExchange.class));
        assertNotEquals(HttpStatus.TOO_MANY_REQUESTS, exchange.getResponse().getStatusCode());
    }
    
    @Test
    void testSetTtlForNewWindow() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response - первый запрос в новом окне
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        // Проверяем, что TTL был установлен для нового окна
        verify(redisTemplate).expire(anyString(), eq(Duration.ofSeconds(1)));
    }
    
    @Test
    void testNoTtlForExistingWindow() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        config.setRequestsPerSecond(10);
        config.setBurstCapacity(20);
        
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/test")
                .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                .build();
        
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        // Mock Redis response - не первый запрос в окне
        when(valueOperations.increment(anyString())).thenReturn(Mono.just(5L));
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        
        // Act
        var filter = rateLimitingFilter.apply(config);
        Mono<Void> result = filter.filter(exchange, filterChain);
        
        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
        
        // Проверяем, что TTL НЕ был установлен для существующего окна
        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }
    
    @Test
    void testConfigurationGettersAndSetters() {
        // Arrange
        RateLimitingFilter.Config config = new RateLimitingFilter.Config();
        
        // Act & Assert - проверяем значения по умолчанию
        assertEquals(100, config.getRequestsPerSecond());
        assertEquals(200, config.getBurstCapacity());
        
        // Act & Assert - проверяем сеттеры
        config.setRequestsPerSecond(50);
        config.setBurstCapacity(100);
        
        assertEquals(50, config.getRequestsPerSecond());
        assertEquals(100, config.getBurstCapacity());
    }
} 