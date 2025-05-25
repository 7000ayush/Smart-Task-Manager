package com.taskmang.service;

import java.util.List;

import com.taskmang.dto.AuditLogDto;

public interface AuditLogService {
    void logAction(String action, String entityType, Long entityId, String oldValue, String newValue, String username);
    List<AuditLogDto> getLogsForEntity(String entityType, Long entityId);
    List<AuditLogDto> getLogsForUser(String username);
}
