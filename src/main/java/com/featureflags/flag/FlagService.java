package com.featureflags.flag;

import com.featureflags.audit.AuditAction;
import com.featureflags.audit.AuditService;
import com.featureflags.config.AuthHelper;
import com.featureflags.flag.dto.CreateFlagRequest;
import com.featureflags.flag.dto.FlagResponse;
import com.featureflags.flag.dto.UpdateFlagRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlagService {

    private final FlagRepository flagRepository;
    private final AuthHelper authHelper;
    private final AuditService auditService;

    public List<FlagResponse> getAll() {
        return flagRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(FlagResponse::from)
                .toList();
    }

    public FlagResponse getByKey(String key) {
        return FlagResponse.from(findByKey(key));
    }

    public FlagResponse create(CreateFlagRequest request) {
        if (flagRepository.existsByKey(request.getKey())) {
            throw new IllegalArgumentException("Flag key already exists: " + request.getKey());
        }

        Flag flag = Flag.builder()
                .key(request.getKey())
                .name(request.getName())
                .description(request.getDescription())
                .environments(request.getEnvironments())
                .tags(request.getTags())
                .createdBy(authHelper.getCurrentUserId())
                .build();

        flagRepository.save(flag);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.CREATED, null, FlagResponse.from(flag));

        return FlagResponse.from(flag);
    }

    public FlagResponse update(String key, UpdateFlagRequest request) {
        Flag flag = findByKey(key);
        FlagResponse before = FlagResponse.from(flag);

        if (request.getName() != null) flag.setName(request.getName());
        if (request.getDescription() != null) flag.setDescription(request.getDescription());
        if (request.getRolloutPercentage() != null) flag.setRolloutPercentage(request.getRolloutPercentage());
        if (request.getEnvironments() != null) flag.setEnvironments(request.getEnvironments());
        if (request.getAllowedUsers() != null) flag.setAllowedUsers(request.getAllowedUsers());
        if (request.getTags() != null) flag.setTags(request.getTags());

        flag.setUpdatedAt(LocalDateTime.now());
        flagRepository.save(flag);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.UPDATED, before, FlagResponse.from(flag));

        return FlagResponse.from(flag);
    }

    public FlagResponse enable(String key) {
        Flag flag = findByKey(key);
        flag.setEnabled(true);
        flag.setUpdatedAt(LocalDateTime.now());
        flagRepository.save(flag);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.ENABLED, null, null);

        return FlagResponse.from(flag);
    }

    public FlagResponse disable(String key) {
        Flag flag = findByKey(key);
        flag.setEnabled(false);
        flag.setUpdatedAt(LocalDateTime.now());
        flagRepository.save(flag);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.DISABLED, null, null);

        return FlagResponse.from(flag);
    }

    public void delete(String key) {
        Flag flag = findByKey(key);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.DELETED, FlagResponse.from(flag), null);

        flagRepository.delete(flag);
    }

    public FlagResponse setRollout(String key, Integer percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Rollout percentage must be between 0 and 100");
        }

        Flag flag = findByKey(key);
        FlagResponse before = FlagResponse.from(flag);

        flag.setRolloutPercentage(percentage);
        flag.setUpdatedAt(LocalDateTime.now());
        flagRepository.save(flag);

        auditService.log(flag.getId(), flag.getKey(), AuditAction.UPDATED, before, FlagResponse.from(flag));

        return FlagResponse.from(flag);
    }

    private Flag findByKey(String key) {
        return flagRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Flag not found: " + key));
    }
}
