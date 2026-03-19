package com.featureflags.flag;

import com.featureflags.flag.dto.CreateFlagRequest;
import com.featureflags.flag.dto.FlagResponse;
import com.featureflags.flag.dto.UpdateFlagRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flags")
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

    @GetMapping
    public ResponseEntity<List<FlagResponse>> getAll() {
        return ResponseEntity.ok(flagService.getAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<FlagResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(flagService.getByKey(key));
    }

    @PostMapping
    public ResponseEntity<FlagResponse> create(@Valid @RequestBody CreateFlagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flagService.create(request));
    }

    @PutMapping("/{key}")
    public ResponseEntity<FlagResponse> update(@PathVariable String key, @Valid @RequestBody UpdateFlagRequest request) {
        return ResponseEntity.ok(flagService.update(key, request));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        flagService.delete(key);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{key}/enable")
    public ResponseEntity<FlagResponse> enable(@PathVariable String key) {
        return ResponseEntity.ok(flagService.enable(key));
    }

    @PatchMapping("/{key}/disable")
    public ResponseEntity<FlagResponse> disable(@PathVariable String key) {
        return ResponseEntity.ok(flagService.disable(key));
    }

    @PatchMapping("/{key}/rollout")
    public ResponseEntity<FlagResponse> setRollout(@PathVariable String key, @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(flagService.setRollout(key, body.get("percentage")));
    }
}
