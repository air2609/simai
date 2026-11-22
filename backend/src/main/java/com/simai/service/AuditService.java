package com.simai.service;

import com.simai.domain.AuditLog;
import com.simai.domain.enums.AuditAction;
import com.simai.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;

    /**
     * Mencatat aktivitas penting (siapa, apa, kapan, nilai sebelum & sesudah).
     * Dipanggil di dalam transaksi service pemanggil agar konsisten.
     */
    public void log(String entityType, String entityId, AuditAction action, Object oldValues, Object newValues) {
        AuditLog log = new AuditLog();
        log.setUser(currentUserService.currentUser());
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOldValues(toJson(oldValues));
        log.setNewValues(toJson(newValues));
        auditLogRepository.save(log);
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
