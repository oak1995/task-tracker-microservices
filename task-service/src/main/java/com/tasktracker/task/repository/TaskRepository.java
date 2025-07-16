package com.tasktracker.task.repository;

import com.tasktracker.task.entity.Task;
import com.tasktracker.task.entity.TaskStatus;
import com.tasktracker.task.entity.TaskPriority;
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
 * Репозиторий для работы с задачами
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Поиск задач по статусу
     */
    List<Task> findByStatus(TaskStatus status);
    
    /**
     * Поиск задач по приоритету
     */
    List<Task> findByPriority(TaskPriority priority);
    
    /**
     * Поиск задач по назначенному пользователю
     */
    List<Task> findByAssignedToUserId(Long userId);
    
    /**
     * Поиск задач по создателю
     */
    List<Task> findByCreatedByUserId(Long userId);
    
    /**
     * Поиск задач по категории
     */
    List<Task> findByCategoryId(Long categoryId);
    
    /**
     * Поиск задач по статусу и назначенному пользователю
     */
    List<Task> findByStatusAndAssignedToUserId(TaskStatus status, Long userId);
    
    /**
     * Поиск задач по множественным статусам
     */
    List<Task> findByStatusIn(List<TaskStatus> statuses);
    
    /**
     * Поиск задач с истекшим сроком выполнения
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Поиск задач по приоритету и статусу
     */
    List<Task> findByPriorityAndStatus(TaskPriority priority, TaskStatus status);
    
    /**
     * Поиск задач по заголовку (частичное совпадение)
     */
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Поиск задач по описанию (частичное совпадение)
     */
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Поиск задач по заголовку или описанию
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Task> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    /**
     * Поиск задач созданных в определенный период
     */
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Поиск задач со сроком выполнения в определенный период
     */
    List<Task> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Количество задач по статусу
     */
    long countByStatus(TaskStatus status);
    
    /**
     * Количество задач по назначенному пользователю
     */
    long countByAssignedToUserId(Long userId);
    
    /**
     * Количество задач по создателю
     */
    long countByCreatedByUserId(Long userId);
    
    /**
     * Проверка существования задачи по заголовку
     */
    boolean existsByTitle(String title);
    
    /**
     * Поиск задач с пагинацией по пользователю
     */
    Page<Task> findByAssignedToUserId(Long userId, Pageable pageable);
    
    /**
     * Поиск задач с пагинацией по создателю
     */
    Page<Task> findByCreatedByUserId(Long userId, Pageable pageable);
    
    /**
     * Поиск задач с пагинацией по статусу
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    
    /**
     * Комплексный поиск задач с фильтрацией
     */
    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
           "(:assignedToUserId IS NULL OR t.assignedToUserId = :assignedToUserId) AND " +
           "(:createdByUserId IS NULL OR t.createdByUserId = :createdByUserId) AND " +
           "(:searchTerm IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findTasksWithFilters(
        @Param("status") TaskStatus status,
        @Param("priority") TaskPriority priority,
        @Param("categoryId") Long categoryId,
        @Param("assignedToUserId") Long assignedToUserId,
        @Param("createdByUserId") Long createdByUserId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable
    );
} 