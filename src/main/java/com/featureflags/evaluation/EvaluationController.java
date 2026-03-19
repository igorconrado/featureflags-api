package com.featureflags.evaluation;

import com.featureflags.evaluation.dto.BulkEvaluationRequest;
import com.featureflags.evaluation.dto.BulkEvaluationResponse;
import com.featureflags.evaluation.dto.EvaluationRequest;
import com.featureflags.evaluation.dto.EvaluationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluate")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<EvaluationResponse> evaluate(@Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(evaluationService.evaluate(
                request.getFlagKey(), request.getUserId(), request.getEnvironment()));
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkEvaluationResponse> evaluateBulk(@Valid @RequestBody BulkEvaluationRequest request) {
        return ResponseEntity.ok(evaluationService.evaluateBulk(
                request.getFlagKeys(), request.getUserId(), request.getEnvironment()));
    }

    @GetMapping("/{flagKey}")
    public ResponseEntity<EvaluationResponse> evaluateGet(
            @PathVariable String flagKey,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false, defaultValue = "production") String environment) {
        return ResponseEntity.ok(evaluationService.evaluate(flagKey, userId, environment));
    }
}
