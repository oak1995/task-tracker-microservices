package com.tasktracker.audit.repository;

import com.tasktracker.audit.entity.AuditUserStats;
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
 * Репозиторий для работы со статистикой пользователей
 */
@Repository
public interface AuditUserStatsRepository extends JpaRepository<AuditUserStats, Long> {
    
    /**
     * Поиск статистики по пользователю
     */
    Page<AuditUserStats> findByUserIdOrderByStatsDateDesc(Long userId, Pageable pageable);
    
    /**
     * Поиск статистики по имени пользователя
     */
    Page<AuditUserStats> findByUsernameContainingIgnoreCaseOrderByStatsDateDesc(String username, Pageable pageable);
    
    /**
     * Поиск статистики за определенную дату
     */
    Optional<AuditUserStats> findByUserIdAndStatsDate(Long userId, LocalDateTime statsDate);
    
    /**
     * Поиск статистики в диапазоне дат
     */
    Page<AuditUserStats> findByStatsDateBetweenOrderByStatsDateDesc(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);
    
    /**
     * Поиск статистики пользователя в диапазоне дат
     */
    Page<AuditUserStats> findByUserIdAndStatsDateBetweenOrderByStatsDateDesc(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Последняя статистика пользователя
     */
    Optional<AuditUserStats> findTopByUserIdOrderByStatsDateDesc(Long userId);
    
    /**
     * Статистика пользователя за сегодня
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.userId = :userId " +
           "AND DATE(s.statsDate) = DATE(:date)")
    Optional<AuditUserStats> findByUserIdAndDate(
            @Param("userId") Long userId, 
            @Param("date") LocalDateTime date);
    
    /**
     * Топ активных пользователей
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.totalActions DESC")
    Page<AuditUserStats> findTopActiveUsersInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Пользователи с самым высоким процентом успешности
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.totalActions > :minActions " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY (s.successfulActions * 100.0 / s.totalActions) DESC")
    Page<AuditUserStats> findTopSuccessfulUsersInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minActions") Integer minActions,
            Pageable pageable);
    
    /**
     * Пользователи с критическими действиями
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.criticalActions > 0 " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.criticalActions DESC")
    Page<AuditUserStats> findUsersWithCriticalActions(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Суммарная статистика по всем пользователям
     */
    @Query("SELECT SUM(s.totalActions), SUM(s.successfulActions), SUM(s.failedActions), " +
           "SUM(s.criticalActions) FROM AuditUserStats s " +
           "WHERE s.statsDate BETWEEN :startDate AND :endDate")
    Object[] getTotalStatsForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Средняя активность пользователей
     */
    @Query("SELECT AVG(s.totalActions), AVG(s.successfulActions), AVG(s.failedActions) " +
           "FROM AuditUserStats s " +
           "WHERE s.statsDate BETWEEN :startDate AND :endDate")
    Object[] getAverageStatsForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Количество активных пользователей
     */
    @Query("SELECT COUNT(DISTINCT s.userId) FROM AuditUserStats s " +
           "WHERE s.totalActions > 0 " +
           "AND s.statsDate BETWEEN :startDate AND :endDate")
    Long countActiveUsersInPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Статистика по дням недели
     */
    @Query("SELECT DAYOFWEEK(s.statsDate) as dayOfWeek, " +
           "AVG(s.totalActions) as avgActions, " +
           "COUNT(s) as recordCount " +
           "FROM AuditUserStats s " +
           "WHERE s.statsDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DAYOFWEEK(s.statsDate) " +
           "ORDER BY dayOfWeek")
    List<Object[]> getWeeklyActivityStats(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Тренд активности пользователя
     */
    @Query("SELECT DATE(s.statsDate) as date, s.totalActions " +
           "FROM AuditUserStats s " +
           "WHERE s.userId = :userId " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.statsDate")
    List<Object[]> getUserActivityTrend(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Пользователи с наибольшим количеством созданных задач
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.tasksCreated > 0 " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.tasksCreated DESC")
    Page<AuditUserStats> findTopTaskCreators(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Пользователи с наибольшим количеством завершенных задач
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.tasksCompleted > 0 " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.tasksCompleted DESC")
    Page<AuditUserStats> findTopTaskCompleters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Статистика логинов/логаутов
     */
    @Query("SELECT s.userId, s.username, SUM(s.loginCount) as totalLogins, " +
           "SUM(s.logoutCount) as totalLogouts " +
           "FROM AuditUserStats s " +
           "WHERE s.statsDate BETWEEN :startDate AND :endDate " +
           "GROUP BY s.userId, s.username " +
           "ORDER BY totalLogins DESC")
    List<Object[]> getLoginStats(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Удаление старой статистики
     */
    @Query("DELETE FROM AuditUserStats s WHERE s.statsDate < :cutoffDate")
    void deleteOldStats(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Поиск пользователей с низкой активностью
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.totalActions < :threshold " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY s.totalActions ASC")
    Page<AuditUserStats> findLowActivityUsers(
            @Param("threshold") Integer threshold,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    /**
     * Поиск пользователей с высокой частотой ошибок
     */
    @Query("SELECT s FROM AuditUserStats s WHERE s.failedActions > 0 " +
           "AND (s.failedActions * 100.0 / s.totalActions) > :errorThreshold " +
           "AND s.statsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY (s.failedActions * 100.0 / s.totalActions) DESC")
    Page<AuditUserStats> findHighErrorRateUsers(
            @Param("errorThreshold") Double errorThreshold,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
} 