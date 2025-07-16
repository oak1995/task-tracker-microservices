package com.tasktracker.task.controller;

import com.tasktracker.task.dto.TaskCreateRequest;
import com.tasktracker.task.dto.TaskResponse;
import com.tasktracker.task.entity.TaskStatus;
import com.tasktracker.task.service.TaskService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления задачами
 * 
 * Этот контроллер предоставляет полный набор HTTP endpoints для работы с задачами:
 * - CRUD операции (Create, Read, Update, Delete)
 * - Поиск и фильтрацию задач
 * - Управление статусами задач
 * - Назначение задач пользователям
 * - Получение статистики
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Management", description = "API для управления задачами")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Создание новой задачи
     * 
     * POST /tasks
     * 
     * Принимает JSON с данными задачи и создает новую задачу в системе.
     * Автоматически устанавливает статус NEW и присваивает создателя.
     */
    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу с указанными параметрами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
        @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        
        logger.info("Creating new task: {} by user: {}", request.getTitle(), userId);
        
        TaskResponse response = taskService.createTask(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получение задачи по ID
     * 
     * GET /tasks/{id}
     * 
     * Возвращает подробную информацию о задаче по её идентификатору.
     */
    @Operation(summary = "Получить задачу по ID", description = "Возвращает подробную информацию о задаче")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача найдена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @Parameter(description = "ID задачи") @PathVariable Long id) {
        
        logger.debug("Getting task by id: {}", id);
        
        TaskResponse response = taskService.getTaskById(id);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение всех задач с пагинацией
     * 
     * GET /tasks?page=0&size=10&sortBy=createdAt&sortDir=desc
     * 
     * Возвращает страницу задач с возможностью пагинации и сортировки.
     */
    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач с пагинацией")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @Parameter(description = "Номер страницы (начинается с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Направление сортировки (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Getting all tasks: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        Page<TaskResponse> response = taskService.getAllTasks(page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение задач назначенных пользователю
     * 
     * GET /tasks/assigned/{userId}
     * 
     * Возвращает задачи, которые назначены конкретному пользователю.
     */
    @Operation(summary = "Получить задачи пользователя", description = "Возвращает задачи назначенные конкретному пользователю")
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByAssignedUser(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Getting tasks by assigned user: {}", userId);
        
        Page<TaskResponse> response = taskService.getTasksByAssignedUser(userId, page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение задач созданных пользователем
     * 
     * GET /tasks/created/{userId}
     * 
     * Возвращает задачи, которые были созданы конкретным пользователем.
     */
    @Operation(summary = "Получить задачи созданные пользователем", description = "Возвращает задачи созданные конкретным пользователем")
    @GetMapping("/created/{userId}")
    public ResponseEntity<Page<TaskResponse>> getTasksByCreator(
            @Parameter(description = "ID создателя") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Getting tasks by creator: {}", userId);
        
        Page<TaskResponse> response = taskService.getTasksByCreator(userId, page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение задач по статусу
     * 
     * GET /tasks/status/{status}
     * 
     * Возвращает задачи с определенным статусом (NEW, IN_PROGRESS, COMPLETED, etc.).
     */
    @Operation(summary = "Получить задачи по статусу", description = "Возвращает задачи с определенным статусом")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TaskResponse>> getTasksByStatus(
            @Parameter(description = "Статус задачи") @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.debug("Getting tasks by status: {}", status);
        
        Page<TaskResponse> response = taskService.getTasksByStatus(status, page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Поиск задач по ключевым словам
     * 
     * GET /tasks/search?q=test
     * 
     * Выполняет поиск задач по названию и описанию.
     */
    @Operation(summary = "Поиск задач", description = "Выполняет поиск задач по названию и описанию")
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(
            @Parameter(description = "Поисковый запрос") @RequestParam String q) {
        
        logger.debug("Searching tasks with query: {}", q);
        
        List<TaskResponse> response = taskService.searchTasks(q);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновление задачи
     * 
     * PUT /tasks/{id}
     * 
     * Обновляет существующую задачу. Только создатель или исполнитель могут обновлять задачу.
     */
    @Operation(summary = "Обновить задачу", description = "Обновляет существующую задачу")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
        @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
        @ApiResponse(responseCode = "403", description = "Нет прав для обновления задачи"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Valid @RequestBody TaskCreateRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        
        logger.info("Updating task: {} by user: {}", id, userId);
        
        TaskResponse response = taskService.updateTask(id, request, userId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Назначение задачи пользователю
     * 
     * POST /tasks/{id}/assign/{userId}
     * 
     * Назначает задачу определенному пользователю и меняет статус на IN_PROGRESS.
     */
    @Operation(summary = "Назначить задачу пользователю", description = "Назначает задачу определенному пользователю")
    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignTask(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        
        logger.info("Assigning task: {} to user: {}", id, userId);
        
        TaskResponse response = taskService.assignTask(id, userId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Изменение статуса задачи
     * 
     * PATCH /tasks/{id}/status
     * 
     * Изменяет статус задачи (NEW -> IN_PROGRESS -> COMPLETED).
     */
    @Operation(summary = "Изменить статус задачи", description = "Изменяет статус задачи")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Parameter(description = "Новый статус") @RequestParam TaskStatus status,
            @RequestHeader("X-User-ID") Long userId) {
        
        logger.info("Updating task status: {} to {} by user: {}", id, status, userId);
        
        TaskResponse response = taskService.updateTaskStatus(id, status, userId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удаление задачи
     * 
     * DELETE /tasks/{id}
     * 
     * Удаляет задачу. Только создатель может удалить задачу.
     */
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу (только создатель)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
        @ApiResponse(responseCode = "403", description = "Нет прав для удаления задачи"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @RequestHeader("X-User-ID") Long userId) {
        
        logger.info("Deleting task: {} by user: {}", id, userId);
        
        taskService.deleteTask(id, userId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Получение просроченных задач
     * 
     * GET /tasks/overdue
     * 
     * Возвращает список задач, срок выполнения которых истек.
     */
    @Operation(summary = "Получить просроченные задачи", description = "Возвращает список просроченных задач")
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        
        logger.debug("Getting overdue tasks");
        
        List<TaskResponse> response = taskService.getOverdueTasks();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получение статистики задач
     * 
     * GET /tasks/statistics
     * 
     * Возвращает статистику по задачам (количество по статусам, просроченные и т.д.).
     */
    @Operation(summary = "Получить статистику задач", description = "Возвращает статистику по задачам")
    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getTaskStatistics() {
        
        logger.debug("Getting task statistics");
        
        TaskService.TaskStatistics response = taskService.getTaskStatistics();
        
        return ResponseEntity.ok(response);
    }
} 