package com.example.featureflag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for flag evaluation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlagEvaluationResponse {
    
    @JsonProperty("flagName")
    private String flagName;
    
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @JsonProperty("userId")
    private String userId;
}
