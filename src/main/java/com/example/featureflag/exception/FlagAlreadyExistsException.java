package com.example.featureflag.exception;

/**
 * Exception thrown when a feature flag already exists
 */
public class FlagAlreadyExistsException extends RuntimeException {
    
    public FlagAlreadyExistsException(String flagName) {
        super(String.format("Feature flag already exists: %s", flagName));
    }
}
