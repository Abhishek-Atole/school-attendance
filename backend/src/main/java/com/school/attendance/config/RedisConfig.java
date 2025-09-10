package com.school.attendance.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Configuration for Caching
 * Provides high-performance caching for frequently accessed data
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
@Slf4j
public class RedisConfig {

    /**
     * Redis Template with JSON serialization
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON serializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), 
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Key serialization
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Value serialization
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        
        log.info("Redis template configured with JSON serialization");
        return template;
    }

    /**
     * Cache Manager with different TTL for different cache types
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // Default TTL: 30 minutes
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Student profiles cache - 1 hour TTL
        cacheConfigurations.put("studentProfiles", 
            defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        
        // Teacher profiles cache - 1 hour TTL  
        cacheConfigurations.put("teacherProfiles", 
            defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        
        // Attendance summaries cache - 15 minutes TTL (more dynamic data)
        cacheConfigurations.put("attendanceSummaries", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Dashboard analytics cache - 5 minutes TTL (very dynamic)
        cacheConfigurations.put("dashboardAnalytics", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Class information cache - 2 hours TTL (relatively static)
        cacheConfigurations.put("classInformation", 
            defaultCacheConfig.entryTtl(Duration.ofHours(2)));
        
        // School configuration cache - 4 hours TTL (very static)
        cacheConfigurations.put("schoolConfiguration", 
            defaultCacheConfig.entryTtl(Duration.ofHours(4)));
        
        // Attendance patterns cache - 30 minutes TTL
        cacheConfigurations.put("attendancePatterns", 
            defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));

        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();

        log.info("Redis cache manager configured with {} cache types", cacheConfigurations.size());
        return cacheManager;
    }

    /**
     * Custom Key Generator for more readable cache keys
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(":");
            sb.append(method.getName()).append(":");
            for (Object param : params) {
                sb.append(param != null ? param.toString() : "null").append(":");
            }
            String key = sb.toString();
            log.debug("Generated cache key: {}", key);
            return key;
        };
    }

    /**
     * Cache Error Handler to prevent cache failures from breaking the application
     */
    @Bean
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException exception, @NonNull org.springframework.cache.Cache cache, @NonNull Object key) {
                log.warn("Cache GET error for cache: {} key: {} - {}", 
                    cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException exception, @NonNull org.springframework.cache.Cache cache, @NonNull Object key, @Nullable Object value) {
                log.warn("Cache PUT error for cache: {} key: {} - {}", 
                    cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException exception, @NonNull org.springframework.cache.Cache cache, @NonNull Object key) {
                log.warn("Cache EVICT error for cache: {} key: {} - {}", 
                    cache.getName(), key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull org.springframework.cache.Cache cache) {
                log.warn("Cache CLEAR error for cache: {} - {}", 
                    cache.getName(), exception.getMessage());
            }
        };
    }
}
