package com.example.featureflag.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

/**
 * AWS DynamoDB configuration
 */
@Configuration
public class DynamoDbConfig {
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.dynamodb.endpoint:}")
    private String dynamoDbEndpoint;
    
    @Bean
    public DynamoDbClient dynamoDbClient() {
        var builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(DefaultCredentialsProvider.create());
        
        // For local testing with DynamoDB Local
        if (dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty()) {
            builder.endpointOverride(URI.create(dynamoDbEndpoint));
        }
        
        return builder.build();
    }
    
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }
}
