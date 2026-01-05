package com.example.featureflag.controller;

import com.example.featureflag.dto.CreateFeatureFlagRequest;
import com.example.featureflag.dto.FeatureFlagResponse;
import com.example.featureflag.dto.FlagEvaluationResponse;
import com.example.featureflag.dto.UpdateFeatureFlagRequest;
import com.example.featureflag.service.FeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for feature flag management
 */
@Slf4j
@RestController
@RequestMapping("/flags")
@RequiredArgsConstructor
@Tag(name = "Feature Flags", description = "Feature flag management API")
public class FeatureFlagController {
    
    private final FeatureFlagService featureFlagService;
    
    @PostMapping
    @Operation(summary = "Create a new feature flag", description = "Creates a new feature flag with the specified configuration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Flag created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Flag already exists")
    })
    public ResponseEntity<FeatureFlagResponse> createFlag(
            @Valid @RequestBody CreateFeatureFlagRequest request) {
        log.info("POST /flags - Creating flag: {}", request.getFlagName());
        FeatureFlagResponse response = featureFlagService.createFlag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{flagName}")
    @Operation(summary = "Get a feature flag", description = "Retrieves a feature flag by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flag found"),
        @ApiResponse(responseCode = "404", description = "Flag not found")
    })
    public ResponseEntity<FeatureFlagResponse> getFlag(
            @Parameter(description = "Name of the feature flag") 
            @PathVariable String flagName) {
        log.info("GET /flags/{} - Retrieving flag", flagName);
        FeatureFlagResponse response = featureFlagService.getFlag(flagName);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{flagName}")
    @Operation(summary = "Update a feature flag", description = "Updates an existing feature flag")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flag updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Flag not found")
    })
    public ResponseEntity<FeatureFlagResponse> updateFlag(
            @Parameter(description = "Name of the feature flag") 
            @PathVariable String flagName,
            @Valid @RequestBody UpdateFeatureFlagRequest request) {
        log.info("PUT /flags/{} - Updating flag", flagName);
        FeatureFlagResponse response = featureFlagService.updateFlag(flagName, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{flagName}")
    @Operation(summary = "Delete a feature flag", description = "Deletes a feature flag by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Flag deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Flag not found")
    })
    public ResponseEntity<Void> deleteFlag(
            @Parameter(description = "Name of the feature flag") 
            @PathVariable String flagName) {
        log.info("DELETE /flags/{} - Deleting flag", flagName);
        featureFlagService.deleteFlag(flagName);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{flagName}/evaluate")
    @Operation(summary = "Evaluate a feature flag", description = "Evaluates if a flag is enabled for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Flag evaluated successfully"),
        @ApiResponse(responseCode = "404", description = "Flag not found")
    })
    public ResponseEntity<FlagEvaluationResponse> evaluateFlag(
            @Parameter(description = "Name of the feature flag") 
            @PathVariable String flagName,
            @Parameter(description = "User ID for evaluation") 
            @RequestParam String userId) {
        log.info("GET /flags/{}/evaluate?userId={} - Evaluating flag", flagName, userId);
        FlagEvaluationResponse response = featureFlagService.evaluateFlag(flagName, userId);
        return ResponseEntity.ok(response);
    }
}
