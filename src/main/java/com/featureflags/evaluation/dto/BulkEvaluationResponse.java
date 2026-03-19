package com.featureflags.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkEvaluationResponse {

    private Map<String, EvaluationResponse> results;
    private String userId;
    private String environment;

    @Builder.Default
    private LocalDateTime evaluatedAt = LocalDateTime.now();
}
