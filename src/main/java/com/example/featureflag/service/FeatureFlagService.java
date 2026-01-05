package com.example.featureflag.service;

import com.example.featureflag.dto.CreateFeatureFlagRequest;
import com.example.featureflag.dto.FeatureFlagResponse;
import com.example.featureflag.dto.FlagEvaluationResponse;
import com.example.featureflag.dto.UpdateFeatureFlagRequest;
import com.example.featureflag.exception.FlagAlreadyExistsException;
import com.example.featureflag.exception.FlagNotFoundException;
import com.example.featureflag.model.FeatureFlag;
import com.example.featureflag.repository.FeatureFlagRepository;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Service for managing feature flags
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureFlagService {
    
    private final FeatureFlagRepository repository;
    private final CacheService cacheService;
    
    /**
     * Create a new feature flag
     */
    public FeatureFlagResponse createFlag(CreateFeatureFlagRequest request) {
        log.info("Creating feature flag: {}", request.getFlagName());
        
        if (repository.existsByFlagName(request.getFlagName())) {
            throw new FlagAlreadyExistsException(request.getFlagName());
        }
        
        FeatureFlag flag = FeatureFlag.builder()
                .flagName(request.getFlagName())
                .enabled(request.getEnabled())
                .rolloutPercentage(request.getRolloutPercentage())
                .description(request.getDescription())
                .version(1)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        FeatureFlag savedFlag = repository.save(flag);
        cacheService.putFlag(savedFlag);
        
        return mapToResponse(savedFlag);
    }
    
    /**
     * Get a feature flag by name
     */
    public FeatureFlagResponse getFlag(String flagName) {
        log.debug("Getting feature flag: {}", flagName);
        
        // Try cache first
        FeatureFlag cachedFlag = cacheService.getFlag(flagName);
        if (cachedFlag != null) {
            log.debug("Flag found in cache: {}", flagName);
            return mapToResponse(cachedFlag);
        }
        
        // Fallback to database
        FeatureFlag flag = repository.findByFlagName(flagName)
                .orElseThrow(() -> new FlagNotFoundException(flagName));
        
        cacheService.putFlag(flag);
        return mapToResponse(flag);
    }
    
    /**
     * Update an existing feature flag
     */
    public FeatureFlagResponse updateFlag(String flagName, UpdateFeatureFlagRequest request) {
        log.info("Updating feature flag: {}", flagName);
        
        FeatureFlag flag = repository.findByFlagName(flagName)
                .orElseThrow(() -> new FlagNotFoundException(flagName));
        
        boolean updated = false;
        
        if (request.getEnabled() != null) {
            flag.setEnabled(request.getEnabled());
            updated = true;
        }
        
        if (request.getRolloutPercentage() != null) {
            flag.setRolloutPercentage(request.getRolloutPercentage());
            updated = true;
        }
        
        if (request.getDescription() != null) {
            flag.setDescription(request.getDescription());
            updated = true;
        }
        
        if (updated) {
            flag.setVersion(flag.getVersion() + 1);
            flag.setUpdatedAt(Instant.now());
        }
        
        FeatureFlag savedFlag = repository.save(flag);
        cacheService.evictFlag(flagName);
        cacheService.putFlag(savedFlag);
        
        return mapToResponse(savedFlag);
    }
    
    /**
     * Delete a feature flag
     */
    public void deleteFlag(String flagName) {
        log.info("Deleting feature flag: {}", flagName);
        
        if (!repository.existsByFlagName(flagName)) {
            throw new FlagNotFoundException(flagName);
        }
        
        repository.deleteByFlagName(flagName);
        cacheService.evictFlag(flagName);
    }
    
    /**
     * Evaluate a feature flag for a specific user
     * Uses deterministic hashing to ensure consistent results
     */
    public FlagEvaluationResponse evaluateFlag(String flagName, String userId) {
        log.debug("Evaluating flag: {} for user: {}", flagName, userId);
        
        FeatureFlag flag = cacheService.getFlag(flagName);
        if (flag == null) {
            flag = repository.findByFlagName(flagName)
                    .orElseThrow(() -> new FlagNotFoundException(flagName));
            cacheService.putFlag(flag);
        }
        
        boolean isEnabled = evaluateFlagForUser(flag, userId);
        
        return FlagEvaluationResponse.builder()
                .flagName(flagName)
                .enabled(isEnabled)
                .userId(userId)
                .build();
    }
    
    /**
     * Evaluate flag using deterministic hashing
     * Same user + flag always gets same result
     */
    private boolean evaluateFlagForUser(FeatureFlag flag, String userId) {
        if (!flag.getEnabled()) {
            return false;
        }
        
        if (flag.getRolloutPercentage() == 100) {
            return true;
        }
        
        if (flag.getRolloutPercentage() == 0) {
            return false;
        }
        
        // Deterministic hash: same user + flag = same result
        String input = flag.getFlagName() + ":" + userId;
        long hash = Hashing.murmur3_128()
                .hashString(input, StandardCharsets.UTF_8)
                .asLong();
        
        // Convert to percentage (0-99)
        int userBucket = Math.abs((int) (hash % 100));
        
        return userBucket < flag.getRolloutPercentage();
    }
    
    private FeatureFlagResponse mapToResponse(FeatureFlag flag) {
        return FeatureFlagResponse.builder()
                .flagName(flag.getFlagName())
                .enabled(flag.getEnabled())
                .rolloutPercentage(flag.getRolloutPercentage())
                .description(flag.getDescription())
                .version(flag.getVersion())
                .createdAt(flag.getCreatedAt())
                .updatedAt(flag.getUpdatedAt())
                .build();
    }
}
