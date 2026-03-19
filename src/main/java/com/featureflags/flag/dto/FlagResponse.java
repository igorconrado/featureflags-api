package com.featureflags.flag.dto;

import com.featureflags.flag.Flag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlagResponse {

    private UUID id;
    private String key;
    private String name;
    private String description;
    private boolean enabled;
    private Integer rolloutPercentage;
    private List<String> environments;
    private List<String> allowedUsers;
    private List<String> tags;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FlagResponse from(Flag flag) {
        return FlagResponse.builder()
                .id(flag.getId())
                .key(flag.getKey())
                .name(flag.getName())
                .description(flag.getDescription())
                .enabled(flag.isEnabled())
                .rolloutPercentage(flag.getRolloutPercentage())
                .environments(flag.getEnvironments())
                .allowedUsers(flag.getAllowedUsers())
                .tags(flag.getTags())
                .createdBy(flag.getCreatedBy())
                .createdAt(flag.getCreatedAt())
                .updatedAt(flag.getUpdatedAt())
                .build();
    }
}
