package com.example.featureflag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing feature flag
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeatureFlagRequest {
    
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @Min(value = 0, message = "Rollout percentage must be between 0 and 100")
    @Max(value = 100, message = "Rollout percentage must be between 0 and 100")
    @JsonProperty("rolloutPercentage")
    private Integer rolloutPercentage;
    
    @JsonProperty("description")
    private String description;
}
