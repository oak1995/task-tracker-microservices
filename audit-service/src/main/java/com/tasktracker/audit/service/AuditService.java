package com.tasktracker.audit.service;

import com.tasktracker.audit.dto.*;
import com.tasktracker.audit.entity.AuditAction;
import com.tasktracker.audit.entity.AuditEvent;
import com.tasktracker.audit.entity.AuditUserStats;
import com.tasktracker.audit.repository.AuditEventRepository;
import com.tasktracker.audit.repository.AuditUserStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы с аудитом
 */
@Service
@Transactional
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    @Autowired
    private AuditEventRepository auditEventRepository;
    
    @Autowired
    private AuditUserStatsRepository auditUserStatsRepository;
    
    /**
     * Создание события аудита
     */
    public AuditEventResponse createAuditEvent(AuditEventRequest request) {
        logger.debug("Создание события аудита для пользователя: {}, действие: {}", 
                    request.getUsername(), request.getAction());
        
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setUserId(request.getUserId());
        auditEvent.setUsername(request.getUsername());
        auditEvent.setAction(request.getAction());
        auditEvent.setEntityType(request.getEntityType());
        auditEvent.setEntityId(request.getEntityId());
        auditEvent.setTimestamp(LocalDateTime.now());
        auditEvent.setIpAddress(request.getIpAddress());
        auditEvent.setUserAgent(request.getUserAgent());
        auditEvent.setSessionId(request.getSessionId());
        auditEvent.setDescription(request.getDescription());
        auditEvent.setOldValues(request.getOldValues());
        auditEvent.setNewValues(request.getNewValues());
        auditEvent.setIsSuccess(request.getIsSuccess());
        auditEvent.setErrorMessage(request.getErrorMessage());
        auditEvent.setServiceName(request.getServiceName());
        auditEvent.setMethodName(request.getMethodName());
        auditEvent.setExecutionTimeMs(request.getExecutionTimeMs());
        
        AuditEvent savedEvent = auditEventRepository.save(auditEvent);
        
        // Обновляем статистику пользователя
        updateUserStats(savedEvent);
        
        logger.info("Создано событие аудита ID: {} для пользователя: {}", 
                   savedEvent.getId(), request.getUsername());
        
        return new AuditEventResponse(savedEvent);
    }
    
    /**
     * Получение события аудита по ID
     */
    @Transactional(readOnly = true)
    public AuditEventResponse getAuditEventById(Long id) {
        logger.debug("Получение события аудита по ID: {}", id);
        
        AuditEvent auditEvent = auditEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Событие аудита не найдено с ID: " + id));
        
        return new AuditEventResponse(auditEvent);
    }
    
    /**
     * Получение событий аудита с фильтрацией
     */
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> getAuditEvents(AuditFilterRequest filter) {
        logger.debug("Получение событий аудита с фильтром: {}", filter);
        
        Pageable pageable = createPageable(filter);
        Page<AuditEvent> events;
        
        if (hasComplexFilter(filter)) {
            events = auditEventRepository.findByComplexFilter(
                    filter.getUserId(),
                    filter.getAction(),
                    filter.getEntityType(),
                    filter.getEntityId(),
                    filter.getStartDate(),
                    filter.getEndDate(),
                    filter.getIsSuccess(),
                    filter.getIpAddress(),
                    filter.getServiceName(),
                    pageable
            );
        } else if (filter.getKeyword() != null && !filter.getKeyword().trim().isEmpty()) {
            events = auditEventRepository.findByDescriptionContaining(filter.getKeyword(), pageable);
        } else if (filter.getCriticalOnly() != null && filter.getCriticalOnly()) {
            List<AuditAction> criticalActions = Arrays.stream(AuditAction.values())
                    .filter(AuditAction::isCritical)
                    .collect(Collectors.toList());
            events = auditEventRepository.findCriticalEvents(criticalActions, pageable);
        } else {
            events = auditEventRepository.findAll(pageable);
        }
        
        return events.map(AuditEventResponse::new);
    }
    
    /**
     * Получение событий аудита по пользователю
     */
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> getAuditEventsByUser(Long userId, int page, int size) {
        logger.debug("Получение событий аудита для пользователя: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditEvent> events = auditEventRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
        
        return events.map(AuditEventResponse::new);
    }
    
    /**
     * Получение событий аудита за период
     */
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> getAuditEventsByPeriod(LocalDateTime startDate, LocalDateTime endDate, 
                                                           int page, int size) {
        logger.debug("Получение событий аудита за период: {} - {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<AuditEvent> events = auditEventRepository.findByTimestampBetweenOrderByTimestampDesc(
                startDate, endDate, pageable);
        
        return events.map(AuditEventResponse::new);
    }
    
    /**
     * Получение критических событий
     */
    @Transactional(readOnly = true)
    public Page<AuditEventResponse> getCriticalEvents(int page, int size) {
        logger.debug("Получение критических событий аудита");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<AuditAction> criticalActions = Arrays.stream(AuditAction.values())
                .filter(AuditAction::isCritical)
                .collect(Collectors.toList());
        
        Page<AuditEvent> events = auditEventRepository.findCriticalEvents(criticalActions, pageable);
        
        return events.map(AuditEventResponse::new);
    }
    
    /**
     * Получение статистики аудита
     */
    @Transactional(readOnly = true)
    public AuditStatisticsResponse getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Получение статистики аудита за период: {} - {}", startDate, endDate);
        
        AuditStatisticsResponse statistics = new AuditStatisticsResponse(startDate, endDate);
        
        // Основная статистика
        Page<AuditEvent> allEvents = auditEventRepository.findByTimestampBetweenOrderByTimestampDesc(
                startDate, endDate, PageRequest.of(0, Integer.MAX_VALUE));
        
        statistics.setTotalEvents((long) allEvents.getContent().size());
        
        long successfulEvents = allEvents.getContent().stream()
                .mapToLong(event -> event.getIsSuccess() ? 1 : 0)
                .sum();
        
        long failedEvents = statistics.getTotalEvents() - successfulEvents;
        
        long criticalEvents = allEvents.getContent().stream()
                .mapToLong(event -> event.isCritical() ? 1 : 0)
                .sum();
        
        statistics.setSuccessfulEvents(successfulEvents);
        statistics.setFailedEvents(failedEvents);
        statistics.setCriticalEvents(criticalEvents);
        
        statistics.calculateRates();
        
        // Количество уникальных пользователей
        Set<Long> uniqueUsers = allEvents.getContent().stream()
                .map(AuditEvent::getUserId)
                .collect(Collectors.toSet());
        statistics.setUniqueUsers(uniqueUsers.size());
        
        // Статистика по действиям
        Map<AuditAction, Long> actionCounts = allEvents.getContent().stream()
                .collect(Collectors.groupingBy(AuditEvent::getAction, Collectors.counting()));
        statistics.setActionCounts(actionCounts);
        
        // Статистика по типам сущностей
        Map<String, Long> entityTypeCounts = allEvents.getContent().stream()
                .filter(event -> event.getEntityType() != null)
                .collect(Collectors.groupingBy(AuditEvent::getEntityType, Collectors.counting()));
        statistics.setEntityTypeCounts(entityTypeCounts);
        
        // Статистика по сервисам
        Map<String, Long> serviceCounts = allEvents.getContent().stream()
                .filter(event -> event.getServiceName() != null)
                .collect(Collectors.groupingBy(AuditEvent::getServiceName, Collectors.counting()));
        statistics.setServiceCounts(serviceCounts);
        
        // Топ активных пользователей
        List<Object[]> topUsers = auditEventRepository.getTopActiveUsers(
                startDate, endDate, PageRequest.of(0, 10));
        
        List<AuditStatisticsResponse.UserActivityStats> userStats = topUsers.stream()
                .map(row -> new AuditStatisticsResponse.UserActivityStats(
                        (Long) row[0], (String) row[1], (Long) row[2]))
                .collect(Collectors.toList());
        statistics.setTopActiveUsers(userStats);
        
        // Статистика времени выполнения
        OptionalDouble avgExecutionTime = allEvents.getContent().stream()
                .filter(event -> event.getExecutionTimeMs() != null)
                .mapToLong(AuditEvent::getExecutionTimeMs)
                .average();
        
        if (avgExecutionTime.isPresent()) {
            statistics.setAverageExecutionTime((long) avgExecutionTime.getAsDouble());
        }
        
        Optional<Long> slowestTime = allEvents.getContent().stream()
                .filter(event -> event.getExecutionTimeMs() != null)
                .map(AuditEvent::getExecutionTimeMs)
                .max(Long::compareTo);
        
        slowestTime.ifPresent(statistics::setSlowestExecutionTime);
        
        Optional<Long> fastestTime = allEvents.getContent().stream()
                .filter(event -> event.getExecutionTimeMs() != null)
                .map(AuditEvent::getExecutionTimeMs)
                .min(Long::compareTo);
        
        fastestTime.ifPresent(statistics::setFastestExecutionTime);
        
        logger.info("Сгенерирована статистика аудита: {} событий, {} пользователей", 
                   statistics.getTotalEvents(), statistics.getUniqueUsers());
        
        return statistics;
    }
    
    /**
     * Удаление старых событий аудита
     */
    public void cleanupOldEvents(LocalDateTime cutoffDate) {
        logger.info("Очистка старых событий аудита до даты: {}", cutoffDate);
        
        auditEventRepository.deleteOldEvents(cutoffDate);
        auditUserStatsRepository.deleteOldStats(cutoffDate);
        
        logger.info("Очистка старых событий аудита завершена");
    }
    
    /**
     * Обновление статистики пользователя
     */
    private void updateUserStats(AuditEvent event) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        AuditUserStats stats = auditUserStatsRepository
                .findByUserIdAndDate(event.getUserId(), today)
                .orElse(new AuditUserStats(event.getUserId(), event.getUsername(), today));
        
        stats.incrementTotalActions();
        
        if (event.getIsSuccess()) {
            stats.incrementSuccessfulActions();
        } else {
            stats.incrementFailedActions();
        }
        
        if (event.isCritical()) {
            stats.incrementCriticalActions();
        }
        
        // Обновляем счетчики по типам действий
        if (event.isUserAction()) {
            stats.setUserActions(stats.getUserActions() + 1);
        } else if (event.isSystemAction()) {
            stats.setSystemActions(stats.getSystemActions() + 1);
        } else if (event.isTaskAction()) {
            stats.setTaskActions(stats.getTaskActions() + 1);
        }
        
        // Обновляем специфические счетчики
        if (event.getAction() == AuditAction.USER_LOGIN) {
            stats.setLoginCount(stats.getLoginCount() + 1);
        } else if (event.getAction() == AuditAction.USER_LOGOUT) {
            stats.setLogoutCount(stats.getLogoutCount() + 1);
        } else if (event.getAction() == AuditAction.TASK_CREATED) {
            stats.setTasksCreated(stats.getTasksCreated() + 1);
        } else if (event.getAction() == AuditAction.TASK_COMPLETED) {
            stats.setTasksCompleted(stats.getTasksCompleted() + 1);
        } else if (event.getAction() == AuditAction.COMMENT_CREATED) {
            stats.setCommentsCreated(stats.getCommentsCreated() + 1);
        }
        
        stats.setLastActivity(event.getTimestamp());
        auditUserStatsRepository.save(stats);
    }
    
    /**
     * Создание объекта Pageable
     */
    private Pageable createPageable(AuditFilterRequest filter) {
        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDirection()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        Sort sort = Sort.by(direction, filter.getSortBy());
        
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }
    
    /**
     * Проверка наличия сложных фильтров
     */
    private boolean hasComplexFilter(AuditFilterRequest filter) {
        return filter.getUserId() != null ||
               filter.getAction() != null ||
               filter.getEntityType() != null ||
               filter.getEntityId() != null ||
               filter.getStartDate() != null ||
               filter.getEndDate() != null ||
               filter.getIsSuccess() != null ||
               filter.getIpAddress() != null ||
               filter.getServiceName() != null;
    }
} 