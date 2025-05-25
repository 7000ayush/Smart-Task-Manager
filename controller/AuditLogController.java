package com.taskmang.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmang.dto.AuditLogDto;
import com.taskmang.service.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for an entity (Admin only)")
    public ResponseEntity<List<AuditLogDto>> getLogsForEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogService.getLogsForEntity(entityType, entityId));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs for a user (Admin only)")
    public ResponseEntity<List<AuditLogDto>> getLogsForUser(
            @PathVariable String username) {
        return ResponseEntity.ok(auditLogService.getLogsForUser(username));
    }
}
