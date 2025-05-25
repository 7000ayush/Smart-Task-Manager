package com.taskmang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskmang.entity.AuditLog;
import com.taskmang.entity.User;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
	List<AuditLog> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, Long entityId);
	List<AuditLog> findByChangedByOrderByChangedAtDesc(User changedBy);

}
