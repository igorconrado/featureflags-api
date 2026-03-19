package com.featureflags.evaluation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationRequest {

    @NotBlank
    private String flagKey;

    private String userId;

    @Builder.Default
    private String environment = "production";
}
