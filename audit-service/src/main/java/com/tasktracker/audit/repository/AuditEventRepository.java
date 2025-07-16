package com.tasktracker.audit.repository;

import com.tasktracker.audit.entity.AuditAction;
import com.tasktracker.audit.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с событиями аудита
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    
    /**
     * Поиск событий по пользователю
     */
    Page<AuditEvent> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    
    /**
     * Поиск событий по имени пользователя
     */
    Page<AuditEvent> findByUsernameContainingIgnoreCaseOrderByTimestampDesc(String username, Pageable pageable);
    
    /**
     * Поиск событий по действию
     */
    Page<AuditEvent> findByActionOrderByTimestampDesc(AuditAction action, Pageable pageable);
    
    /**
     * Поиск событий по типу сущности
     */
    Page<AuditEvent> findByEntityTypeOrderByTimestampDesc(String entityType, Pageable pageable);
    
    /**
     * Поиск событий по ID сущности
     */
    Page<AuditEvent> findByEntityIdOrderByTimestampDesc(Long entityId, Pageable pageable);
    
    /**
     * Поиск событий в диапазоне дат
     */
    Page<AuditEvent> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Поиск событий по успешности
     */
    Page<AuditEvent> findByIsSuccessOrderByTimestampDesc(Boolean isSuccess, Pageable pageable);
    
    /**
     * Поиск критических событий
     */
    @Query("SELECT a FROM AuditEvent a WHERE a.action IN :criticalActions ORDER BY a.timestamp DESC")
    Page<AuditEvent> findCriticalEvents(@Param("criticalActions") List<AuditAction> criticalActions, Pageable pageable);
    
    /**
     * Поиск событий пользователя за период
     */
    @Query("SELECT a FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> findByUserIdAndTimestampBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Поиск событий по действию и пользователю
     */
    @Query("SELECT a FROM AuditEvent a WHERE a.userId = :userId AND a.action = :action " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> findByUserIdAndAction(
            @Param("userId") Long userId,
            @Param("action") AuditAction action,
            Pageable pageable);
    
    /**
     * Поиск событий с IP адресом
     */
    Page<AuditEvent> findByIpAddressOrderByTimestampDesc(String ipAddress, Pageable pageable);
    
    /**
     * Поиск событий по сервису
     */
    Page<AuditEvent> findByServiceNameOrderByTimestampDesc(String serviceName, Pageable pageable);
    
    /**
     * Комплексный поиск событий
     */
    @Query("SELECT a FROM AuditEvent a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR a.timestamp <= :endDate) AND " +
           "(:isSuccess IS NULL OR a.isSuccess = :isSuccess) AND " +
           "(:ipAddress IS NULL OR a.ipAddress = :ipAddress) AND " +
           "(:serviceName IS NULL OR a.serviceName = :serviceName) " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> findByComplexFilter(
            @Param("userId") Long userId,
            @Param("action") AuditAction action,
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("isSuccess") Boolean isSuccess,
            @Param("ipAddress") String ipAddress,
            @Param("serviceName") String serviceName,
            Pageable pageable);
    
    /**
     * Поиск по описанию
     */
    @Query("SELECT a FROM AuditEvent a WHERE a.description LIKE %:keyword% " +
           "ORDER BY a.timestamp DESC")
    Page<AuditEvent> findByDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Статистика по действиям пользователя
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "GROUP BY a.action ORDER BY COUNT(a) DESC")
    List<Object[]> getUserActionStats(@Param("userId") Long userId);
    
    /**
     * Статистика по успешности действий
     */
    @Query("SELECT a.isSuccess, COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "GROUP BY a.isSuccess")
    List<Object[]> getUserSuccessStats(@Param("userId") Long userId);
    
    /**
     * Количество событий пользователя за день
     */
    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "AND DATE(a.timestamp) = DATE(:date)")
    Long countUserEventsForDate(@Param("userId") Long userId, @Param("date") LocalDateTime date);
    
    /**
     * Последнее событие пользователя
     */
    Optional<AuditEvent> findTopByUserIdOrderByTimestampDesc(Long userId);
    
    /**
     * Количество неуспешных событий пользователя за период
     */
    @Query("SELECT COUNT(a) FROM AuditEvent a WHERE a.userId = :userId " +
           "AND a.isSuccess = false " +
           "AND a.timestamp BETWEEN :startDate AND :endDate")
    Long countFailedEventsForUser(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Топ активных пользователей
     */
    @Query("SELECT a.userId, a.username, COUNT(a) as eventCount FROM AuditEvent a " +
           "WHERE a.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY a.userId, a.username " +
           "ORDER BY eventCount DESC")
    List<Object[]> getTopActiveUsers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Статистика по типам сущностей
     */
    @Query("SELECT a.entityType, COUNT(a) FROM AuditEvent a " +
           "WHERE a.entityType IS NOT NULL " +
           "GROUP BY a.entityType ORDER BY COUNT(a) DESC")
    List<Object[]> getEntityTypeStats();
    
    /**
     * Удаление старых событий
     */
    @Query("DELETE FROM AuditEvent a WHERE a.timestamp < :cutoffDate")
    void deleteOldEvents(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Поиск событий с длительным выполнением
     */
    @Query("SELECT a FROM AuditEvent a WHERE a.executionTimeMs > :threshold " +
           "ORDER BY a.executionTimeMs DESC")
    Page<AuditEvent> findSlowEvents(@Param("threshold") Long threshold, Pageable pageable);
} 