package PG_CRUD.repository;

import PG_CRUD.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    
    List<ActivityLog> findByUserId(Long userId);
    
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);
    
    List<ActivityLog> findByUsername(String username);
    
    Page<ActivityLog> findByUsername(String username, Pageable pageable);
    
    List<ActivityLog> findByAction(ActivityLog.Action action);
    
    Page<ActivityLog> findByAction(ActivityLog.Action action, Pageable pageable);
    
    List<ActivityLog> findByEntityType(ActivityLog.EntityType entityType);
    
    Page<ActivityLog> findByEntityType(ActivityLog.EntityType entityType, Pageable pageable);
    
    List<ActivityLog> findByUserIdAndAction(Long userId, ActivityLog.Action action);
    
    List<ActivityLog> findByEntityTypeAndEntityId(ActivityLog.EntityType entityType, Long entityId);
    
    @Query("SELECT a FROM ActivityLog a WHERE " +
           "LOWER(a.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.details) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ActivityLog> searchActivityLogs(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT a FROM ActivityLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<ActivityLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM ActivityLog a WHERE a.userId = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<ActivityLog> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM ActivityLog a WHERE a.action = :action AND a.createdAt BETWEEN :startDate AND :endDate")
    List<ActivityLog> findByActionAndCreatedAtBetween(@Param("action") ActivityLog.Action action,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM ActivityLog a WHERE a.entityType = :entityType AND a.action = :action")
    List<ActivityLog> findByEntityTypeAndAction(@Param("entityType") ActivityLog.EntityType entityType,
                                               @Param("action") ActivityLog.Action action);
    
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.action = :action")
    long countByAction(@Param("action") ActivityLog.Action action);
    
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.entityType = :entityType")
    long countByEntityType(@Param("entityType") ActivityLog.EntityType entityType);
    
    @Query("SELECT a FROM ActivityLog a WHERE a.ipAddress = :ipAddress")
    List<ActivityLog> findByIpAddress(@Param("ipAddress") String ipAddress);
    
    @Query("SELECT DISTINCT a.ipAddress FROM ActivityLog a WHERE a.createdAt >= :since")
    List<String> findDistinctIpAddressesSince(@Param("since") LocalDateTime since);
}
