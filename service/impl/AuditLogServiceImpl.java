package com.taskmang.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskmang.dto.AuditLogDto;
import com.taskmang.entity.AuditLog;
import com.taskmang.entity.User;
import com.taskmang.repository.AuditLogRepository;
import com.taskmang.repository.UserRepository;
import com.taskmang.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void logAction(String action, String entityType, Long entityId, 
                        String oldValue, String newValue, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setChangedBy(user);

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDto> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId)
                .stream()
                .map(log -> modelMapper.map(log, AuditLogDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDto> getLogsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return auditLogRepository.findByChangedByOrderByChangedAtDesc(user)
                .stream()
                .map(log -> modelMapper.map(log, AuditLogDto.class))
                .collect(Collectors.toList());
    }
}
