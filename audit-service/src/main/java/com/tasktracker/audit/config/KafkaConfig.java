package com.tasktracker.audit.config;

import com.tasktracker.audit.dto.AuditEventRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka для Audit Service
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    /**
     * Конфигурация потребителя Kafka
     */
    @Bean
    public ConsumerFactory<String, AuditEventRequest> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Основные настройки
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Настройки для обработки JSON
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AuditEventRequest.class.getName());
        
        // Настройки производительности
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        
        // Настройки надежности
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 40000);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
    
    /**
     * Фабрика контейнеров для слушателей Kafka
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuditEventRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AuditEventRequest> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        factory.setConsumerFactory(consumerFactory());
        
        // Настройки контейнера
        factory.setConcurrency(3); // Количество потоков для обработки
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(3000);
        
        // Обработка ошибок
        //factory.setCommonErrorHandler(new DefaultErrorHandler());
        //factory.setErrorHandler((exception, data) -> {
        //    // Логирование ошибки
        //    System.err.println("Ошибка в Kafka consumer: " + exception.getMessage());
        //    System.err.println("Данные: " + data);
        //});
        
        return factory;
    }
    
    /**
     * Конфигурация производителя Kafka (для отправки событий)
     */
    @Bean
    public ProducerFactory<String, AuditEventRequest> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Основные настройки
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Настройки производительности
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        // Настройки надежности
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * Шаблон Kafka для отправки сообщений
     */
    @Bean
    public KafkaTemplate<String, AuditEventRequest> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
} 