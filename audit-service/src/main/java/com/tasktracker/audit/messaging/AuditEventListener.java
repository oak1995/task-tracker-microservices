package com.tasktracker.audit.messaging;

import com.tasktracker.audit.dto.AuditEventRequest;
import com.tasktracker.audit.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Kafka listener для получения событий аудита от других сервисов
 */
@Component
public class AuditEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Обработка событий от Auth Service
     */
    @KafkaListener(topics = "auth-events", groupId = "audit-service")
    public void handleAuthEvent(@Payload AuditEventRequest event,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        
        logger.info("Получено событие аудита от Auth Service: topic={}, event={}", 
                   topic, event);
        
        try {
            event.setServiceName("auth-service");
            auditService.createAuditEvent(event);
            
            logger.info("Событие аудита от Auth Service успешно обработано: {}", event.getAction());
        } catch (Exception e) {
            logger.error("Ошибка при обработке события аудита от Auth Service: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Обработка событий от Task Service
     */
    @KafkaListener(topics = "task-events", groupId = "audit-service")
    public void handleTaskEvent(@Payload AuditEventRequest event,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Получено событие аудита от Task Service: topic={}, partition={}, offset={}, event={}", 
                   topic, partition, offset, event);
        
        try {
            event.setServiceName("task-service");
            auditService.createAuditEvent(event);
            
            logger.info("Событие аудита от Task Service успешно обработано: {}", event.getAction());
        } catch (Exception e) {
            logger.error("Ошибка при обработке события аудита от Task Service: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Обработка общих системных событий
     */
    @KafkaListener(topics = "system-events", groupId = "audit-service")
    public void handleSystemEvent(@Payload AuditEventRequest event,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Получено системное событие аудита: topic={}, partition={}, offset={}, event={}", 
                   topic, partition, offset, event);
        
        try {
            if (event.getServiceName() == null) {
                event.setServiceName("system");
            }
            auditService.createAuditEvent(event);
            
            logger.info("Системное событие аудита успешно обработано: {}", event.getAction());
        } catch (Exception e) {
            logger.error("Ошибка при обработке системного события аудита: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Обработка событий от Gateway Service
     */
    @KafkaListener(topics = "gateway-events", groupId = "audit-service")
    public void handleGatewayEvent(@Payload AuditEventRequest event,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Получено событие аудита от Gateway Service: topic={}, partition={}, offset={}, event={}", 
                   topic, partition, offset, event);
        
        try {
            event.setServiceName("gateway-service");
            auditService.createAuditEvent(event);
            
            logger.info("Событие аудита от Gateway Service успешно обработано: {}", event.getAction());
        } catch (Exception e) {
            logger.error("Ошибка при обработке события аудита от Gateway Service: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Обработка событий от Notification Service
     */
    @KafkaListener(topics = "notification-events", groupId = "audit-service")
    public void handleNotificationEvent(@Payload AuditEventRequest event,
                                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                       @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Получено событие аудита от Notification Service: topic={}, partition={}, offset={}, event={}", 
                   topic, partition, offset, event);
        
        try {
            event.setServiceName("notification-service");
            auditService.createAuditEvent(event);
            
            logger.info("Событие аудита от Notification Service успешно обработано: {}", event.getAction());
        } catch (Exception e) {
            logger.error("Ошибка при обработке события аудита от Notification Service: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Обработка критических событий безопасности
     */
    @KafkaListener(topics = "security-events", groupId = "audit-service")
    public void handleSecurityEvent(@Payload AuditEventRequest event,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.warn("Получено критическое событие безопасности: topic={}, partition={}, offset={}, event={}", 
                   topic, partition, offset, event);
        
        try {
            event.setServiceName("security-monitor");
            auditService.createAuditEvent(event);
            
            logger.warn("Критическое событие безопасности успешно обработано: {}", event.getAction());
            
            // Дополнительная обработка для критических событий
            if (event.getAction().isCritical()) {
                handleCriticalSecurityEvent(event);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке критического события безопасности: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Дополнительная обработка критических событий безопасности
     */
    private void handleCriticalSecurityEvent(AuditEventRequest event) {
        logger.error("КРИТИЧЕСКОЕ СОБЫТИЕ БЕЗОПАСНОСТИ: {} - {}", 
                    event.getAction(), event.getDescription());
        
        // Здесь можно добавить:
        // - Отправку уведомлений администраторам
        // - Блокировку пользователя
        // - Сохранение в отдельную таблицу критических событий
        // - Интеграцию с системами мониторинга
    }
} 