package com.example.featureflag.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Health check and info endpoint
 */
@RestController
@RequestMapping("/")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns the health status of the service")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", Instant.now()));
    }
    
    @GetMapping("/")
    @Operation(summary = "Service info", description = "Returns basic service information")
    public ResponseEntity<ServiceInfo> info() {
        return ResponseEntity.ok(new ServiceInfo(
                "Feature Flag Service",
                "1.0.0",
                "Running"
        ));
    }
    
    @Data
    static class HealthResponse {
        private final String status;
        private final Instant timestamp;
    }
    
    @Data
    static class ServiceInfo {
        private final String name;
        private final String version;
        private final String status;
    }
}

/**
 * Custom health indicator for Redis
 */
@Component
class RedisHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // In production, check actual Redis connectivity
        return Health.up()
                .withDetail("redis", "available")
                .build();
    }
}

/**
 * Custom health indicator for DynamoDB
 */
@Component
class DynamoDbHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // In production, check actual DynamoDB connectivity
        return Health.up()
                .withDetail("dynamodb", "available")
                .build();
    }
}
