package com.featureflags.flag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String key;

    @Column(nullable = false)
    private String name;

    private String description;

    @Builder.Default
    private boolean enabled = false;

    @Column(name = "rollout_percentage")
    @Builder.Default
    private Integer rolloutPercentage = 0;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "environments", columnDefinition = "text[]")
    @Builder.Default
    private List<String> environments = List.of();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "allowed_users", columnDefinition = "text[]")
    @Builder.Default
    private List<String> allowedUsers = List.of();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", columnDefinition = "text[]")
    @Builder.Default
    private List<String> tags = List.of();

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
