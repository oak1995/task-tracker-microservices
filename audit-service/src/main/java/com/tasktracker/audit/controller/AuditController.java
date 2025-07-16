package com.tasktracker.audit.controller;

import com.tasktracker.audit.dto.AuditEventRequest;
import com.tasktracker.audit.dto.AuditEventResponse;
import com.tasktracker.audit.dto.AuditFilterRequest;
import com.tasktracker.audit.dto.AuditStatisticsResponse;
import com.tasktracker.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST контроллер для работы с аудитом
 */
@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit", description = "API для работы с аудитом системы")
public class AuditController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Создание события аудита
     */
    @PostMapping("/events")
    @Operation(summary = "Создание события аудита", description = "Создает новое событие аудита в системе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Событие аудита успешно создано"),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditEventResponse> createAuditEvent(
            @Valid @RequestBody AuditEventRequest request) {
        
        logger.debug("Получен запрос на создание события аудита: {}", request);
        
        AuditEventResponse response = auditService.createAuditEvent(request);
        
        logger.info("Создано событие аудита с ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получение события аудита по ID
     */
    @GetMapping("/events/{id}")
    @Operation(summary = "Получение события аудита", description = "Возвращает событие аудита по его ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Событие аудита найдено"),
        @ApiResponse(responseCode = "404", description = "Событие аудита не найдено"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditEventResponse> getAuditEvent(
            @Parameter(description = "ID события аудита", required = true)
            @PathVariable Long id) {
        
        logger.debug("Получен запрос на получение события аудита с ID: {}", id);
        
        AuditEventResponse response = auditService.getAuditEventById(id);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение событий аудита с фильтрацией
     */
    @GetMapping("/events")
    @Operation(summary = "Получение событий аудита", description = "Возвращает список событий аудита с возможностью фильтрации")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список событий аудита получен"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры фильтрации"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Page<AuditEventResponse>> getAuditEvents(
            @Parameter(description = "Фильтр для поиска событий")
            AuditFilterRequest filter) {
        
        logger.debug("Получен запрос на получение событий аудита с фильтром: {}", filter);
        
        Page<AuditEventResponse> response = auditService.getAuditEvents(filter);
        
        logger.info("Найдено {} событий аудита", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение событий аудита по пользователю
     */
    @GetMapping("/events/user/{userId}")
    @Operation(summary = "Получение событий пользователя", description = "Возвращает события аудита для конкретного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "События пользователя получены"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Page<AuditEventResponse>> getAuditEventsByUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Получен запрос на получение событий аудита для пользователя: {}", userId);
        
        Page<AuditEventResponse> response = auditService.getAuditEventsByUser(userId, page, size);
        
        logger.info("Найдено {} событий аудита для пользователя {}", response.getTotalElements(), userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение событий аудита за период
     */
    @GetMapping("/events/period")
    @Operation(summary = "Получение событий за период", description = "Возвращает события аудита за указанный временной период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "События за период получены"),
        @ApiResponse(responseCode = "400", description = "Неверные даты периода"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Page<AuditEventResponse>> getAuditEventsByPeriod(
            @Parameter(description = "Дата начала периода", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Получен запрос на получение событий аудита за период: {} - {}", startDate, endDate);
        
        Page<AuditEventResponse> response = auditService.getAuditEventsByPeriod(startDate, endDate, page, size);
        
        logger.info("Найдено {} событий аудита за период", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение критических событий аудита
     */
    @GetMapping("/events/critical")
    @Operation(summary = "Получение критических событий", description = "Возвращает критические события аудита")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Критические события получены"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Page<AuditEventResponse>> getCriticalEvents(
            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Получен запрос на получение критических событий аудита");
        
        Page<AuditEventResponse> response = auditService.getCriticalEvents(page, size);
        
        logger.info("Найдено {} критических событий аудита", response.getTotalElements());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение статистики аудита
     */
    @GetMapping("/statistics")
    @Operation(summary = "Получение статистики аудита", description = "Возвращает статистику аудита за указанный период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика аудита получена"),
        @ApiResponse(responseCode = "400", description = "Неверные параметры периода"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditStatisticsResponse> getAuditStatistics(
            @Parameter(description = "Дата начала периода", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        logger.debug("Получен запрос на получение статистики аудита за период: {} - {}", startDate, endDate);
        
        AuditStatisticsResponse response = auditService.getAuditStatistics(startDate, endDate);
        
        logger.info("Сгенерирована статистика аудита за период: {} событий", response.getTotalEvents());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение статистики аудита за сегодня
     */
    @GetMapping("/statistics/today")
    @Operation(summary = "Получение статистики за сегодня", description = "Возвращает статистику аудита за текущий день")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика за сегодня получена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditStatisticsResponse> getTodayStatistics() {
        
        logger.debug("Получен запрос на получение статистики аудита за сегодня");
        
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        AuditStatisticsResponse response = auditService.getAuditStatistics(startOfDay, endOfDay);
        
        logger.info("Сгенерирована статистика аудита за сегодня: {} событий", response.getTotalEvents());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение статистики аудита за неделю
     */
    @GetMapping("/statistics/week")
    @Operation(summary = "Получение статистики за неделю", description = "Возвращает статистику аудита за последнюю неделю")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика за неделю получена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditStatisticsResponse> getWeekStatistics() {
        
        logger.debug("Получен запрос на получение статистики аудита за неделю");
        
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();
        
        AuditStatisticsResponse response = auditService.getAuditStatistics(weekAgo, now);
        
        logger.info("Сгенерирована статистика аудита за неделю: {} событий", response.getTotalEvents());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение статистики аудита за месяц
     */
    @GetMapping("/statistics/month")
    @Operation(summary = "Получение статистики за месяц", description = "Возвращает статистику аудита за последний месяц")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статистика за месяц получена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<AuditStatisticsResponse> getMonthStatistics() {
        
        logger.debug("Получен запрос на получение статистики аудита за месяц");
        
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        LocalDateTime now = LocalDateTime.now();
        
        AuditStatisticsResponse response = auditService.getAuditStatistics(monthAgo, now);
        
        logger.info("Сгенерирована статистика аудита за месяц: {} событий", response.getTotalEvents());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Очистка старых событий аудита
     */
    @DeleteMapping("/events/cleanup")
    @Operation(summary = "Очистка старых событий", description = "Удаляет события аудита старше указанной даты")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Старые события успешно удалены"),
        @ApiResponse(responseCode = "400", description = "Неверная дата отсечки"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<String> cleanupOldEvents(
            @Parameter(description = "Дата отсечки для удаления старых событий", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cutoffDate) {
        
        logger.debug("Получен запрос на очистку старых событий аудита до даты: {}", cutoffDate);
        
        auditService.cleanupOldEvents(cutoffDate);
        
        logger.info("Выполнена очистка старых событий аудита до даты: {}", cutoffDate);
        return ResponseEntity.ok("Старые события аудита успешно удалены");
    }
    
    /**
     * Проверка работоспособности сервиса
     */
    @GetMapping("/health")
    @Operation(summary = "Проверка работоспособности", description = "Проверяет работоспособность сервиса аудита")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сервис работает нормально"),
        @ApiResponse(responseCode = "500", description = "Сервис недоступен")
    })
    public ResponseEntity<String> healthCheck() {
        
        logger.debug("Получен запрос на проверку работоспособности сервиса аудита");
        
        return ResponseEntity.ok("Audit Service is healthy");
    }
} 