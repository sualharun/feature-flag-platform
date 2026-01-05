package com.example.featureflag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

/**
 * Feature Flag entity stored in DynamoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class FeatureFlag {
    
    private String flagName;
    private Boolean enabled;
    private Integer rolloutPercentage;
    private String description;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("flagName")
    public String getFlagName() {
        return flagName;
    }
    
    @DynamoDbAttribute("enabled")
    public Boolean getEnabled() {
        return enabled;
    }
    
    @DynamoDbAttribute("rolloutPercentage")
    public Integer getRolloutPercentage() {
        return rolloutPercentage;
    }
    
    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }
    
    @DynamoDbAttribute("version")
    public Integer getVersion() {
        return version;
    }
    
    @DynamoDbAttribute("createdAt")
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    @DynamoDbAttribute("updatedAt")
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
