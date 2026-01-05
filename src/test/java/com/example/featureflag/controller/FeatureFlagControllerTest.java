package com.example.featureflag.controller;

import com.example.featureflag.dto.CreateFeatureFlagRequest;
import com.example.featureflag.dto.FeatureFlagResponse;
import com.example.featureflag.dto.FlagEvaluationResponse;
import com.example.featureflag.dto.UpdateFeatureFlagRequest;
import com.example.featureflag.exception.FlagAlreadyExistsException;
import com.example.featureflag.exception.FlagNotFoundException;
import com.example.featureflag.service.FeatureFlagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeatureFlagController.class)
class FeatureFlagControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private FeatureFlagService service;
    
    @Test
    void createFlag_Success() throws Exception {
        CreateFeatureFlagRequest request = CreateFeatureFlagRequest.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .description("Test flag")
                .build();
        
        FeatureFlagResponse response = FeatureFlagResponse.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .description("Test flag")
                .version(1)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        when(service.createFlag(any(CreateFeatureFlagRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.flagName").value("test_flag"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.rolloutPercentage").value(50));
    }
    
    @Test
    void createFlag_ValidationError() throws Exception {
        CreateFeatureFlagRequest request = CreateFeatureFlagRequest.builder()
                .flagName("")
                .enabled(true)
                .rolloutPercentage(150) // Invalid
                .build();
        
        mockMvc.perform(post("/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createFlag_AlreadyExists() throws Exception {
        CreateFeatureFlagRequest request = CreateFeatureFlagRequest.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .build();
        
        when(service.createFlag(any(CreateFeatureFlagRequest.class)))
                .thenThrow(new FlagAlreadyExistsException("test_flag"));
        
        mockMvc.perform(post("/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    
    @Test
    void getFlag_Success() throws Exception {
        FeatureFlagResponse response = FeatureFlagResponse.builder()
                .flagName("test_flag")
                .enabled(true)
                .rolloutPercentage(50)
                .version(1)
                .build();
        
        when(service.getFlag("test_flag")).thenReturn(response);
        
        mockMvc.perform(get("/flags/test_flag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagName").value("test_flag"))
                .andExpect(jsonPath("$.enabled").value(true));
    }
    
    @Test
    void getFlag_NotFound() throws Exception {
        when(service.getFlag("test_flag")).thenThrow(new FlagNotFoundException("test_flag"));
        
        mockMvc.perform(get("/flags/test_flag"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void updateFlag_Success() throws Exception {
        UpdateFeatureFlagRequest request = UpdateFeatureFlagRequest.builder()
                .enabled(false)
                .rolloutPercentage(75)
                .build();
        
        FeatureFlagResponse response = FeatureFlagResponse.builder()
                .flagName("test_flag")
                .enabled(false)
                .rolloutPercentage(75)
                .version(2)
                .build();
        
        when(service.updateFlag(anyString(), any(UpdateFeatureFlagRequest.class)))
                .thenReturn(response);
        
        mockMvc.perform(put("/flags/test_flag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.rolloutPercentage").value(75));
    }
    
    @Test
    void deleteFlag_Success() throws Exception {
        mockMvc.perform(delete("/flags/test_flag"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void evaluateFlag_Success() throws Exception {
        FlagEvaluationResponse response = FlagEvaluationResponse.builder()
                .flagName("test_flag")
                .enabled(true)
                .userId("user123")
                .build();
        
        when(service.evaluateFlag("test_flag", "user123")).thenReturn(response);
        
        mockMvc.perform(get("/flags/test_flag/evaluate")
                .param("userId", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagName").value("test_flag"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.userId").value("user123"));
    }
}
