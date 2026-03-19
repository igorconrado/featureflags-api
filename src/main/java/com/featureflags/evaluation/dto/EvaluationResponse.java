package com.featureflags.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    private String flagKey;
    private boolean enabled;
    private String reason;
    private String userId;
    private String environment;

    @Builder.Default
    private LocalDateTime evaluatedAt = LocalDateTime.now();
}
