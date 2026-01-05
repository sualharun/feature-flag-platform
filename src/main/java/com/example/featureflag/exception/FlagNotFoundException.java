package com.example.featureflag.exception;

/**
 * Exception thrown when a feature flag is not found
 */
public class FlagNotFoundException extends RuntimeException {
    
    public FlagNotFoundException(String flagName) {
        super(String.format("Feature flag not found: %s", flagName));
    }
}
