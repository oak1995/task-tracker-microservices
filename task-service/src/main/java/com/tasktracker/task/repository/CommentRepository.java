package com.tasktracker.task.repository;

import com.tasktracker.task.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с комментариями
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * Поиск комментариев по задаче
     */
    List<Comment> findByTaskId(Long taskId);
    
    /**
     * Поиск комментариев по задаче с пагинацией
     */
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);
    
    /**
     * Поиск комментариев по автору
     */
    List<Comment> findByAuthorUserId(Long authorUserId);
    
    /**
     * Поиск комментариев по автору с пагинацией
     */
    Page<Comment> findByAuthorUserId(Long authorUserId, Pageable pageable);
    
    /**
     * Поиск комментариев по задаче и автору
     */
    List<Comment> findByTaskIdAndAuthorUserId(Long taskId, Long authorUserId);
    
    /**
     * Поиск комментариев по содержимому (частичное совпадение)
     */
    List<Comment> findByContentContainingIgnoreCase(String content);
    
    /**
     * Поиск комментариев созданных в определенный период
     */
    List<Comment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Количество комментариев по задаче
     */
    long countByTaskId(Long taskId);
    
    /**
     * Количество комментариев по автору
     */
    long countByAuthorUserId(Long authorUserId);
    
    /**
     * Поиск последних комментариев по задаче
     */
    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<Comment> findLatestCommentsByTaskId(@Param("taskId") Long taskId, Pageable pageable);
    
    /**
     * Поиск комментариев по задаче, отсортированных по дате создания
     */
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
    
    /**
     * Поиск комментариев по задаче, отсортированных по дате создания (убывание)
     */
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    /**
     * Удаление комментариев по задаче
     */
    void deleteByTaskId(Long taskId);
    
    /**
     * Удаление комментариев по автору
     */
    void deleteByAuthorUserId(Long authorUserId);
} 