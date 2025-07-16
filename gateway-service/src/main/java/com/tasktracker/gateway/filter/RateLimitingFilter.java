package com.tasktracker.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Rate Limiting Filter для Gateway
 * 
 * Ограничивает количество запросов от одного клиента в единицу времени
 */
@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;
    
    public RateLimitingFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            String key = "rate_limit:" + clientId;
            
            return checkRateLimit(key, config.getRequestsPerSecond(), config.getBurstCapacity())
                    .flatMap(allowed -> {
                        if (allowed) {
                            logger.debug("Запрос разрешен для клиента: {}", clientId);
                            return chain.filter(exchange);
                        } else {
                            logger.warn("Rate limit превышен для клиента: {}", clientId);
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            exchange.getResponse().getHeaders().add("X-RateLimit-Limit", 
                                    String.valueOf(config.getRequestsPerSecond()));
                            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                            exchange.getResponse().getHeaders().add("Retry-After", "1");
                            return exchange.getResponse().setComplete();
                        }
                    });
        };
    }
    
    /**
     * Получает идентификатор клиента (IP адрес или User ID)
     */
    private String getClientId(org.springframework.web.server.ServerWebExchange exchange) {
        // Сначала пробуем получить User ID из заголовка
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (userId != null) {
            return "user:" + userId;
        }
        
        // Если User ID нет, используем IP адрес
        String clientIp = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : 
                "unknown";
        
        return "ip:" + clientIp;
    }
    
    /**
     * Проверяет лимит запросов с использованием Redis
     */
    private Mono<Boolean> checkRateLimit(String key, int requestsPerSecond, int burstCapacity) {
        long currentTime = Instant.now().getEpochSecond();
        String windowKey = key + ":" + currentTime;
        
        return redisTemplate.opsForValue()
                .increment(windowKey)
                .flatMap(count -> {
                    if (count == 1) {
                        // Устанавливаем TTL для окна
                        return redisTemplate.expire(windowKey, Duration.ofSeconds(1))
                                .thenReturn(true);
                    } else {
                        // Проверяем лимит
                        return Mono.just(count <= requestsPerSecond);
                    }
                })
                .onErrorReturn(false); // В случае ошибки Redis - разрешаем запрос
    }
    
    /**
     * Конфигурация Rate Limiting
     */
    public static class Config {
        private int requestsPerSecond = 100;
        private int burstCapacity = 200;
        
        public int getRequestsPerSecond() {
            return requestsPerSecond;
        }
        
        public void setRequestsPerSecond(int requestsPerSecond) {
            this.requestsPerSecond = requestsPerSecond;
        }
        
        public int getBurstCapacity() {
            return burstCapacity;
        }
        
        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
        }
    }
} 