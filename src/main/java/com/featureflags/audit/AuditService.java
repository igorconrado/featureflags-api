package com.featureflags.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflags.auth.User;
import com.featureflags.config.AuthHelper;
import com.featureflags.flag.FlagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final FlagRepository flagRepository;
    private final AuthHelper authHelper;
    private final ObjectMapper objectMapper;

    public void log(UUID flagId, String flagKey, AuditAction action, Object before, Object after) {
        UUID userId = null;
        String userEmail = null;

        try {
            User user = authHelper.getCurrentUser();
            userId = user.getId();
            userEmail = user.getEmail();
        } catch (Exception ignored) {
        }

        String changes = null;
        try {
            Map<String, Object> changesMap = new HashMap<>();
            if (before != null) changesMap.put("before", before);
            if (after != null) changesMap.put("after", after);
            if (!changesMap.isEmpty()) {
                changes = objectMapper.writeValueAsString(changesMap);
            }
        } catch (Exception e) {
            log.warn("Failed to serialize audit changes", e);
        }

        AuditLog auditLog = AuditLog.builder()
                .flagId(flagId)
                .flagKey(flagKey)
                .userId(userId)
                .userEmail(userEmail)
                .action(action)
                .changes(changes)
                .build();

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getByFlagKey(String key) {
        var flag = flagRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Flag not found: " + key));
        return auditLogRepository.findByFlagIdOrderByCreatedAtDesc(flag.getId());
    }

    public List<AuditLog> getAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
