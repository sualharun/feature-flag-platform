package com.example.featureflag.service;

import com.example.featureflag.dto.CreateFeatureFlagRequest;
import com.example.featureflag.dto.FeatureFlagResponse;
import com.example.featureflag.dto.FlagEvaluationResponse;
import com.example.featureflag.dto.UpdateFeatureFlagRequest;
import com.example.featureflag.exception.FlagAlreadyExistsException;
import com.example.featureflag.exception.FlagNotFoundException;
import com.example.featureflag.model.FeatureFlag;
import com.example.featureflag.repository.FeatureFlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {
    
    @Mock
    private FeatureFlagRepository repository;
    
    @Mock
    private CacheService cacheService;
    
    @InjectMocks
    private FeatureFlagService service;
    
    private FeatureFlag testFlag;
    
    @BeforeEach
    void setUp() {
        testFlag = FeatureFlag.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .description("Test flag")
                .version(1)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
    
    @Test
    void createFlag_Success() {
        CreateFeatureFlagRequest request = CreateFeatureFlagRequest.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .description("Test flag")
                .build();
        
        when(repository.existsByFlagName(anyString())).thenReturn(false);
        when(repository.save(any(FeatureFlag.class))).thenReturn(testFlag);
        
        FeatureFlagResponse response = service.createFlag(request);
        
        assertNotNull(response);
        assertEquals("test_flag", response.getFlagName());
        assertEquals(true, response.getEnabled());
        assertEquals(50, response.getRolloutPercentage());
        
        verify(repository).save(any(FeatureFlag.class));
        verify(cacheService).putFlag(any(FeatureFlag.class));
    }
    
    @Test
    void createFlag_AlreadyExists() {
        CreateFeatureFlagRequest request = CreateFeatureFlagRequest.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .build();
        
        when(repository.existsByFlagName("test_flag")).thenReturn(true);
        
        assertThrows(FlagAlreadyExistsException.class, () -> service.createFlag(request));
        verify(repository, never()).save(any());
    }
    
    @Test
    void getFlag_FromCache() {
        when(cacheService.getFlag("test_flag")).thenReturn(testFlag);
        
        FeatureFlagResponse response = service.getFlag("test_flag");
        
        assertNotNull(response);
        assertEquals("test_flag", response.getFlagName());
        verify(repository, never()).findByFlagName(anyString());
    }
    
    @Test
    void getFlag_FromDatabase() {
        when(cacheService.getFlag("test_flag")).thenReturn(null);
        when(repository.findByFlagName("test_flag")).thenReturn(Optional.of(testFlag));
        
        FeatureFlagResponse response = service.getFlag("test_flag");
        
        assertNotNull(response);
        assertEquals("test_flag", response.getFlagName());
        verify(repository).findByFlagName("test_flag");
        verify(cacheService).putFlag(testFlag);
    }
    
    @Test
    void getFlag_NotFound() {
        when(cacheService.getFlag("test_flag")).thenReturn(null);
        when(repository.findByFlagName("test_flag")).thenReturn(Optional.empty());
        
        assertThrows(FlagNotFoundException.class, () -> service.getFlag("test_flag"));
    }
    
    @Test
    void updateFlag_Success() {
        UpdateFeatureFlagRequest request = UpdateFeatureFlagRequest.builder()
                .enabled(false)
                .rolloutPercentage(75)
                .build();
        
        when(repository.findByFlagName("test_flag")).thenReturn(Optional.of(testFlag));
        when(repository.save(any(FeatureFlag.class))).thenReturn(testFlag);
        
        FeatureFlagResponse response = service.updateFlag("test_flag", request);
        
        assertNotNull(response);
        verify(repository).save(any(FeatureFlag.class));
        verify(cacheService).evictFlag("test_flag");
        verify(cacheService).putFlag(any(FeatureFlag.class));
    }
    
    @Test
    void deleteFlag_Success() {
        when(repository.existsByFlagName("test_flag")).thenReturn(true);
        
        service.deleteFlag("test_flag");
        
        verify(repository).deleteByFlagName("test_flag");
        verify(cacheService).evictFlag("test_flag");
    }
    
    @Test
    void deleteFlag_NotFound() {
        when(repository.existsByFlagName("test_flag")).thenReturn(false);
        
        assertThrows(FlagNotFoundException.class, () -> service.deleteFlag("test_flag"));
        verify(repository, never()).deleteByFlagName(anyString());
    }
    
    @Test
    void evaluateFlag_DisabledFlag() {
        testFlag.setEnabled(false);
        when(cacheService.getFlag("test_flag")).thenReturn(testFlag);
        
        FlagEvaluationResponse response = service.evaluateFlag("test_flag", "user123");
        
        assertNotNull(response);
        assertFalse(response.getEnabled());
    }
    
    @Test
    void evaluateFlag_100PercentRollout() {
        testFlag.setRolloutPercentage(100);
        when(cacheService.getFlag("test_flag")).thenReturn(testFlag);
        
        FlagEvaluationResponse response = service.evaluateFlag("test_flag", "user123");
        
        assertNotNull(response);
        assertTrue(response.getEnabled());
    }
    
    @Test
    void evaluateFlag_0PercentRollout() {
        testFlag.setRolloutPercentage(0);
        when(cacheService.getFlag("test_flag")).thenReturn(testFlag);
        
        FlagEvaluationResponse response = service.evaluateFlag("test_flag", "user123");
        
        assertNotNull(response);
        assertFalse(response.getEnabled());
    }
    
    @Test
    void evaluateFlag_DeterministicHashing() {
        testFlag.setRolloutPercentage(50);
        when(cacheService.getFlag("test_flag")).thenReturn(testFlag);
        
        // Same user should always get same result
        FlagEvaluationResponse response1 = service.evaluateFlag("test_flag", "user123");
        FlagEvaluationResponse response2 = service.evaluateFlag("test_flag", "user123");
        
        assertEquals(response1.getEnabled(), response2.getEnabled());
    }
}
