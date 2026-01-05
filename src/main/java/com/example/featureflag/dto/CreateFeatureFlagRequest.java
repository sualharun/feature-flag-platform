package com.example.featureflag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new feature flag
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeatureFlagRequest {
    
    @NotBlank(message = "Flag name is required")
    @JsonProperty("flagName")
    private String flagName;
    
    @NotNull(message = "Enabled status is required")
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @NotNull(message = "Rollout percentage is required")
    @Min(value = 0, message = "Rollout percentage must be between 0 and 100")
    @Max(value = 100, message = "Rollout percentage must be between 0 and 100")
    @JsonProperty("rolloutPercentage")
    private Integer rolloutPercentage;
    
    @JsonProperty("description")
    private String description;
}
