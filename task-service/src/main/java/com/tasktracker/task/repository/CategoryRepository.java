package com.tasktracker.task.repository;

import com.tasktracker.task.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями
 * 
 * @author Orazbakhov Aibek
 * @version 1.0
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Поиск категории по имени
     */
    Optional<Category> findByName(String name);
    
    /**
     * Поиск активных категорий
     */
    List<Category> findByIsActiveTrue();
    
    /**
     * Поиск неактивных категорий
     */
    List<Category> findByIsActiveFalse();
    
    /**
     * Поиск категорий по статусу активности
     */
    List<Category> findByIsActive(Boolean isActive);
    
    /**
     * Поиск категорий по частичному совпадению имени
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * Поиск категорий по частичному совпадению описания
     */
    List<Category> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Проверка существования категории по имени
     */
    boolean existsByName(String name);
    
    /**
     * Проверка существования категории по имени (исключая текущую)
     */
    boolean existsByNameAndIdNot(String name, Long id);
    
    /**
     * Количество активных категорий
     */
    long countByIsActiveTrue();
    
    /**
     * Количество неактивных категорий
     */
    long countByIsActiveFalse();
    
    /**
     * Поиск категорий с количеством задач
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.tasks WHERE c.isActive = true")
    List<Category> findActiveCategoriesWithTasks();
    
    /**
     * Поиск категорий с количеством задач больше указанного
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.tasks) > :taskCount")
    List<Category> findCategoriesWithMoreTasks(@Param("taskCount") int taskCount);
    
    /**
     * Поиск категорий без задач
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.tasks) = 0")
    List<Category> findCategoriesWithoutTasks();
} 