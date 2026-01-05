package com.example.featureflag.service;

import com.example.featureflag.model.FeatureFlag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Redis cache service for feature flags
 */
@Slf4j
@Service
public class CacheService {
    
    @Value("${spring.redis.host:localhost}")
    private String redisHost;
    
    @Value("${spring.redis.port:6379}")
    private int redisPort;
    
    @Value("${cache.ttl.seconds:300}")
    private int cacheTtlSeconds;
    
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;
    
    @PostConstruct
    public void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        
        this.jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        log.info("Initialized Redis cache at {}:{}", redisHost, redisPort);
    }
    
    @PreDestroy
    public void destroy() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            log.info("Closed Redis connection pool");
        }
    }
    
    /**
     * Get a feature flag from cache
     */
    public FeatureFlag getFlag(String flagName) {
        try (var jedis = jedisPool.getResource()) {
            String key = getCacheKey(flagName);
            String value = jedis.get(key);
            
            if (value == null) {
                log.debug("Cache miss for flag: {}", flagName);
                return null;
            }
            
            log.debug("Cache hit for flag: {}", flagName);
            return objectMapper.readValue(value, FeatureFlag.class);
        } catch (JedisException | JsonProcessingException e) {
            log.warn("Error reading from cache for flag: {}", flagName, e);
            return null; // Graceful degradation
        }
    }
    
    /**
     * Put a feature flag into cache
     */
    public void putFlag(FeatureFlag flag) {
        try (var jedis = jedisPool.getResource()) {
            String key = getCacheKey(flag.getFlagName());
            String value = objectMapper.writeValueAsString(flag);
            
            jedis.setex(key, cacheTtlSeconds, value);
            log.debug("Cached flag: {} with TTL: {}s", flag.getFlagName(), cacheTtlSeconds);
        } catch (JedisException | JsonProcessingException e) {
            log.warn("Error writing to cache for flag: {}", flag.getFlagName(), e);
            // Don't throw - caching is optional
        }
    }
    
    /**
     * Evict a feature flag from cache
     */
    public void evictFlag(String flagName) {
        try (var jedis = jedisPool.getResource()) {
            String key = getCacheKey(flagName);
            jedis.del(key);
            log.debug("Evicted flag from cache: {}", flagName);
        } catch (JedisException e) {
            log.warn("Error evicting from cache for flag: {}", flagName, e);
        }
    }
    
    private String getCacheKey(String flagName) {
        return "flag:" + flagName;
    }
}
