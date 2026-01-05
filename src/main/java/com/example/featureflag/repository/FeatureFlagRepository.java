package com.example.featureflag.repository;

import com.example.featureflag.model.FeatureFlag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * Repository for FeatureFlag operations with DynamoDB
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FeatureFlagRepository {
    
    private final DynamoDbEnhancedClient enhancedClient;
    
    @Value("${aws.dynamodb.table-name:feature-flags}")
    private String tableName;
    
    private DynamoDbTable<FeatureFlag> table;
    
    @PostConstruct
    public void init() {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(FeatureFlag.class));
        log.info("Initialized FeatureFlagRepository with table: {}", tableName);
    }
    
    /**
     * Save or update a feature flag
     */
    public FeatureFlag save(FeatureFlag featureFlag) {
        try {
            table.putItem(featureFlag);
            log.debug("Saved feature flag: {}", featureFlag.getFlagName());
            return featureFlag;
        } catch (DynamoDbException e) {
            log.error("Error saving feature flag: {}", featureFlag.getFlagName(), e);
            throw new RuntimeException("Failed to save feature flag", e);
        }
    }
    
    /**
     * Find a feature flag by name
     */
    public Optional<FeatureFlag> findByFlagName(String flagName) {
        try {
            Key key = Key.builder()
                    .partitionValue(flagName)
                    .build();
            
            FeatureFlag flag = table.getItem(key);
            return Optional.ofNullable(flag);
        } catch (DynamoDbException e) {
            log.error("Error finding feature flag: {}", flagName, e);
            throw new RuntimeException("Failed to find feature flag", e);
        }
    }
    
    /**
     * Delete a feature flag by name
     */
    public void deleteByFlagName(String flagName) {
        try {
            Key key = Key.builder()
                    .partitionValue(flagName)
                    .build();
            
            table.deleteItem(key);
            log.debug("Deleted feature flag: {}", flagName);
        } catch (DynamoDbException e) {
            log.error("Error deleting feature flag: {}", flagName, e);
            throw new RuntimeException("Failed to delete feature flag", e);
        }
    }
    
    /**
     * Check if a feature flag exists
     */
    public boolean existsByFlagName(String flagName) {
        return findByFlagName(flagName).isPresent();
    }
}
