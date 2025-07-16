package com.tasktracker.task.service;

import com.tasktracker.task.dto.TaskCreateRequest;
import com.tasktracker.task.dto.TaskResponse;
import com.tasktracker.task.entity.Task;
import com.tasktracker.task.entity.TaskStatus;
import com.tasktracker.task.entity.TaskPriority;
import com.tasktracker.task.entity.Category;
import com.tasktracker.task.repository.TaskRepository;
import com.tasktracker.task.repository.CategoryRepository;
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
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления задачами
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Создание новой задачи
     */
    public TaskResponse createTask(TaskCreateRequest request, Long createdByUserId) {
        logger.info("Creating new task: {} by user: {}", request.getTitle(), createdByUserId);
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setCreatedByUserId(createdByUserId);
        task.setStatus(TaskStatus.NEW);
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setAssignedToUserId(request.getAssignedToUserId());
        
        // Установка категории, если указана
        if (request.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());
            if (category.isPresent() && category.get().getIsActive()) {
                task.setCategory(category.get());
            } else {
                logger.warn("Category with id {} not found or inactive", request.getCategoryId());
            }
        }
        
        // Автоматическое изменение статуса при назначении
        if (request.getAssignedToUserId() != null) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with id: {}", savedTask.getId());
        
        return convertToResponse(savedTask);
    }
    
    /**
     * Получение задачи по ID
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        logger.debug("Getting task by id: {}", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        return convertToResponse(task);
    }
    
    /**
     * Получение всех задач с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting all tasks: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> tasks = taskRepository.findAll(pageable);
        
        return tasks.map(this::convertToResponse);
    }
    
    /**
     * Получение задач по назначенному пользователю
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByAssignedUser(Long userId, int page, int size) {
        logger.debug("Getting tasks by assigned user: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> tasks = taskRepository.findByAssignedToUserId(userId, pageable);
        
        return tasks.map(this::convertToResponse);
    }
    
    /**
     * Получение задач по создателю
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByCreator(Long userId, int page, int size) {
        logger.debug("Getting tasks by creator: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> tasks = taskRepository.findByCreatedByUserId(userId, pageable);
        
        return tasks.map(this::convertToResponse);
    }
    
    /**
     * Получение задач по статусу
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByStatus(TaskStatus status, int page, int size) {
        logger.debug("Getting tasks by status: {}", status);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Task> tasks = taskRepository.findByStatus(status, pageable);
        
        return tasks.map(this::convertToResponse);
    }
    
    /**
     * Поиск задач
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String searchTerm) {
        logger.debug("Searching tasks with term: {}", searchTerm);
        
        List<Task> tasks = taskRepository.searchByTitleOrDescription(searchTerm);
        
        return tasks.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Обновление задачи
     */
    public TaskResponse updateTask(Long id, TaskCreateRequest request, Long updatedByUserId) {
        logger.info("Updating task: {} by user: {}", id, updatedByUserId);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Проверка прав доступа
        if (!task.getCreatedByUserId().equals(updatedByUserId) && 
            !task.getAssignedToUserId().equals(updatedByUserId)) {
            throw new RuntimeException("Access denied: User can only update own tasks");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        
        // Обновление категории
        if (request.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(request.getCategoryId());
            if (category.isPresent() && category.get().getIsActive()) {
                task.setCategory(category.get());
            }
        } else {
            task.setCategory(null);
        }
        
        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully: {}", updatedTask.getId());
        
        return convertToResponse(updatedTask);
    }
    
    /**
     * Назначение задачи пользователю
     */
    public TaskResponse assignTask(Long id, Long userId) {
        logger.info("Assigning task: {} to user: {}", id, userId);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.assignTo(userId);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Task assigned successfully: {}", updatedTask.getId());
        return convertToResponse(updatedTask);
    }
    
    /**
     * Изменение статуса задачи
     */
    public TaskResponse updateTaskStatus(Long id, TaskStatus newStatus, Long updatedByUserId) {
        logger.info("Updating task status: {} to {} by user: {}", id, newStatus, updatedByUserId);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Проверка прав доступа
        if (!task.getCreatedByUserId().equals(updatedByUserId) && 
            !task.getAssignedToUserId().equals(updatedByUserId)) {
            throw new RuntimeException("Access denied: User can only update own tasks");
        }
        
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        
        logger.info("Task status updated successfully: {}", updatedTask.getId());
        return convertToResponse(updatedTask);
    }
    
    /**
     * Удаление задачи
     */
    public void deleteTask(Long id, Long deletedByUserId) {
        logger.info("Deleting task: {} by user: {}", id, deletedByUserId);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Проверка прав доступа
        if (!task.getCreatedByUserId().equals(deletedByUserId)) {
            throw new RuntimeException("Access denied: Only task creator can delete task");
        }
        
        taskRepository.delete(task);
        logger.info("Task deleted successfully: {}", id);
    }
    
    /**
     * Получение просроченных задач
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        logger.debug("Getting overdue tasks");
        
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        
        return overdueTasks.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Получение статистики задач
     */
    @Transactional(readOnly = true)
    public TaskStatistics getTaskStatistics() {
        logger.debug("Getting task statistics");
        
        TaskStatistics stats = new TaskStatistics();
        stats.setTotalTasks(taskRepository.count());
        stats.setNewTasks(taskRepository.countByStatus(TaskStatus.NEW));
        stats.setInProgressTasks(taskRepository.countByStatus(TaskStatus.IN_PROGRESS));
        stats.setCompletedTasks(taskRepository.countByStatus(TaskStatus.COMPLETED));
        stats.setCancelledTasks(taskRepository.countByStatus(TaskStatus.CANCELLED));
        stats.setOverdueTasks(taskRepository.findOverdueTasks(LocalDateTime.now()).size());
        
        return stats;
    }
    
    /**
     * Конвертация Task в TaskResponse
     */
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setAssignedToUserId(task.getAssignedToUserId());
        response.setCreatedByUserId(task.getCreatedByUserId());
        response.setDueDate(task.getDueDate());
        response.setEstimatedHours(task.getEstimatedHours());
        response.setActualHours(task.getActualHours());
        response.setCommentsCount(task.getCommentsCount());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        
        // Конвертация категории
        if (task.getCategory() != null) {
            // Создание CategoryResponse будет в CategoryService
        }
        
        return response;
    }
    
    /**
     * Внутренний класс для статистики
     */
    public static class TaskStatistics {
        private long totalTasks;
        private long newTasks;
        private long inProgressTasks;
        private long completedTasks;
        private long cancelledTasks;
        private long overdueTasks;
        
        // Getters and Setters
        public long getTotalTasks() { return totalTasks; }
        public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
        public long getNewTasks() { return newTasks; }
        public void setNewTasks(long newTasks) { this.newTasks = newTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public void setInProgressTasks(long inProgressTasks) { this.inProgressTasks = inProgressTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
        public long getCancelledTasks() { return cancelledTasks; }
        public void setCancelledTasks(long cancelledTasks) { this.cancelledTasks = cancelledTasks; }
        public long getOverdueTasks() { return overdueTasks; }
        public void setOverdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; }
    }
} 