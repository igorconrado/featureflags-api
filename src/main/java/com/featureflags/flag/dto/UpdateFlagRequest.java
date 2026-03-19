package com.featureflags.flag.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class UpdateFlagRequest {

    private String name;

    private String description;

    @Min(0)
    @Max(100)
    private Integer rolloutPercentage;

    private List<String> environments;

    private List<String> allowedUsers;

    private List<String> tags;
}
