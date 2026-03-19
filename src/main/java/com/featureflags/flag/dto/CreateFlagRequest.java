package com.featureflags.flag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFlagRequest {

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Key must contain only lowercase letters, numbers, and hyphens")
    private String key;

    @NotBlank
    private String name;

    private String description;

    @Builder.Default
    private List<String> environments = new ArrayList<>();

    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
