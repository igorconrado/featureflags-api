package com.featureflags.evaluation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkEvaluationRequest {

    @NotEmpty
    private List<String> flagKeys;

    private String userId;

    @Builder.Default
    private String environment = "production";
}
