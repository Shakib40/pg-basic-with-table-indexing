package PG_CRUD.service;

import PG_CRUD.entity.ActivityLog;
import PG_CRUD.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityLogService {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
    public Page<ActivityLog> getAllActivityLogs(Pageable pageable) {
        return activityLogRepository.findAll(pageable);
    }
    
    public Optional<ActivityLog> getActivityLogById(Long id) {
        return activityLogRepository.findById(id);
    }
    
    public List<ActivityLog> getActivityLogsByUserId(Long userId) {
        return activityLogRepository.findByUserId(userId);
    }
    
    public Page<ActivityLog> getActivityLogsByUserId(Long userId, Pageable pageable) {
        return activityLogRepository.findByUserId(userId, pageable);
    }
    
    public List<ActivityLog> getActivityLogsByUsername(String username) {
        return activityLogRepository.findByUsername(username);
    }
    
    public Page<ActivityLog> getActivityLogsByUsername(String username, Pageable pageable) {
        return activityLogRepository.findByUsername(username, pageable);
    }
    
    public List<ActivityLog> getActivityLogsByAction(ActivityLog.Action action) {
        return activityLogRepository.findByAction(action);
    }
    
    public Page<ActivityLog> getActivityLogsByAction(ActivityLog.Action action, Pageable pageable) {
        return activityLogRepository.findByAction(action, pageable);
    }
    
    public List<ActivityLog> getActivityLogsByEntityType(ActivityLog.EntityType entityType) {
        return activityLogRepository.findByEntityType(entityType);
    }
    
    public Page<ActivityLog> getActivityLogsByEntityType(ActivityLog.EntityType entityType, Pageable pageable) {
        return activityLogRepository.findByEntityType(entityType, pageable);
    }
    
    public List<ActivityLog> getActivityLogsByUserIdAndAction(Long userId, ActivityLog.Action action) {
        return activityLogRepository.findByUserIdAndAction(userId, action);
    }
    
    public List<ActivityLog> getActivityLogsByEntityTypeAndEntityId(ActivityLog.EntityType entityType, Long entityId) {
        return activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    public Page<ActivityLog> searchActivityLogs(String searchTerm, Pageable pageable) {
        return activityLogRepository.searchActivityLogs(searchTerm, pageable);
    }
    
    public List<ActivityLog> getActivityLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    public List<ActivityLog> getActivityLogsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }
    
    public List<ActivityLog> getActivityLogsByActionAndDateRange(ActivityLog.Action action, LocalDateTime startDate, LocalDateTime endDate) {
        return activityLogRepository.findByActionAndCreatedAtBetween(action, startDate, endDate);
    }
    
    public List<ActivityLog> getActivityLogsByEntityTypeAndAction(ActivityLog.EntityType entityType, ActivityLog.Action action) {
        return activityLogRepository.findByEntityTypeAndAction(entityType, action);
    }
    
    public List<ActivityLog> getActivityLogsByIpAddress(String ipAddress) {
        return activityLogRepository.findByIpAddress(ipAddress);
    }
    
    public List<String> getDistinctIpAddressesSince(LocalDateTime since) {
        return activityLogRepository.findDistinctIpAddressesSince(since);
    }
    
    public long getActivityLogCount() {
        return activityLogRepository.count();
    }
    
    public long getActivityLogCountByAction(ActivityLog.Action action) {
        return activityLogRepository.countByAction(action);
    }
    
    public long getActivityLogCountByEntityType(ActivityLog.EntityType entityType) {
        return activityLogRepository.countByEntityType(entityType);
    }
    
    public ActivityLog createActivityLog(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }
    
    public void logActivity(Long userId, String username, ActivityLog.Action action, 
                          ActivityLog.EntityType entityType, Long entityId, String description, 
                          String ipAddress, String userAgent) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        
        activityLogRepository.save(log);
    }
}
