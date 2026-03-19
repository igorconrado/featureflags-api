package com.featureflags.evaluation;

import com.featureflags.evaluation.dto.BulkEvaluationResponse;
import com.featureflags.evaluation.dto.EvaluationResponse;
import com.featureflags.flag.Flag;
import com.featureflags.flag.FlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final FlagRepository flagRepository;

    public EvaluationResponse evaluate(String flagKey, String userId, String environment) {
        if (environment == null || environment.isBlank()) {
            environment = "production";
        }

        var flagOpt = flagRepository.findByKey(flagKey);

        if (flagOpt.isEmpty()) {
            return buildResponse(flagKey, false, "Flag not found", userId, environment);
        }

        Flag flag = flagOpt.get();

        if (!flag.isEnabled()) {
            return buildResponse(flagKey, false, "Flag is disabled", userId, environment);
        }

        if (userId != null && flag.getAllowedUsers().contains(userId)) {
            return buildResponse(flagKey, true, "User in allowed list", userId, environment);
        }

        if (!flag.getEnvironments().isEmpty() && !flag.getEnvironments().contains(environment)) {
            return buildResponse(flagKey, false, "Environment not allowed", userId, environment);
        }

        if (flag.getRolloutPercentage() == 100) {
            return buildResponse(flagKey, true, "Full rollout", userId, environment);
        }

        if (flag.getRolloutPercentage() == 0) {
            return buildResponse(flagKey, false, "Rollout at 0%", userId, environment);
        }

        if (userId != null) {
            int bucket = Math.abs((flagKey + userId).hashCode()) % 100;
            if (bucket < flag.getRolloutPercentage()) {
                return buildResponse(flagKey, true, "In rollout", userId, environment);
            } else {
                return buildResponse(flagKey, false, "Not in rollout", userId, environment);
            }
        }

        return buildResponse(flagKey, false, "No userId provided for rollout evaluation", userId, environment);
    }

    public BulkEvaluationResponse evaluateBulk(List<String> flagKeys, String userId, String environment) {
        Map<String, EvaluationResponse> results = new LinkedHashMap<>();
        for (String key : flagKeys) {
            results.put(key, evaluate(key, userId, environment));
        }

        return BulkEvaluationResponse.builder()
                .results(results)
                .userId(userId)
                .environment(environment != null && !environment.isBlank() ? environment : "production")
                .build();
    }

    private EvaluationResponse buildResponse(String flagKey, boolean enabled, String reason, String userId, String environment) {
        return EvaluationResponse.builder()
                .flagKey(flagKey)
                .enabled(enabled)
                .reason(reason)
                .userId(userId)
                .environment(environment)
                .build();
    }
}
