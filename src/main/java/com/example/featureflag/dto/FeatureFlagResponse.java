package com.example.featureflag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for feature flag data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureFlagResponse {
    
    @JsonProperty("flagName")
    private String flagName;
    
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @JsonProperty("rolloutPercentage")
    private Integer rolloutPercentage;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("version")
    private Integer version;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("updatedAt")
    private Instant updatedAt;
}
