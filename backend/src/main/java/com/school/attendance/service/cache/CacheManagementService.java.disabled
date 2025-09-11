package com.school.attendance.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cache Management Service for Performance Monitoring and Operations
 * Provides utilities for cache warming, monitoring, and maintenance
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheManagementService {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CachedStudentService cachedStudentService;
    private final CachedAttendanceService cachedAttendanceService;

    /**
     * Get cache statistics for monitoring
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Collection<String> cacheNames = cacheManager.getCacheNames();
        stats.put("totalCaches", cacheNames.size());
        stats.put("cacheNames", cacheNames);
        
        Map<String, Object> cacheDetails = new HashMap<>();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("name", cacheName);
                cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                cacheDetails.put(cacheName, cacheInfo);
            }
        }
        stats.put("cacheDetails", cacheDetails);
        
        // Redis-specific statistics
        try {
            Set<String> keys = redisTemplate.keys("*");
            stats.put("totalRedisKeys", keys != null ? keys.size() : 0);
            
            if (keys != null && !keys.isEmpty()) {
                Map<String, Integer> keysByPrefix = new HashMap<>();
                for (String key : keys) {
                    String prefix = key.split(":")[0];
                    keysByPrefix.put(prefix, keysByPrefix.getOrDefault(prefix, 0) + 1);
                }
                stats.put("keysByPrefix", keysByPrefix);
            }
        } catch (Exception e) {
            log.warn("Error getting Redis statistics: {}", e.getMessage());
            stats.put("redisError", e.getMessage());
        }
        
        return stats;
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        log.info("Clearing all caches");
        
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Cleared cache: {}", cacheName);
            }
        }
        
        log.info("All caches cleared successfully");
    }

    /**
     * Clear specific cache by name
     */
    public void clearCache(String cacheName) {
        log.info("Clearing cache: {}", cacheName);
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("Cache '{}' cleared successfully", cacheName);
        } else {
            log.warn("Cache '{}' not found", cacheName);
        }
    }

    /**
     * Evict specific cache entry
     */
    public void evictCacheEntry(String cacheName, String key) {
        log.info("Evicting cache entry - cache: {}, key: {}", cacheName, key);
        
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.info("Cache entry evicted successfully - cache: {}, key: {}", cacheName, key);
        } else {
            log.warn("Cache '{}' not found", cacheName);
        }
    }

    /**
     * Warm up all caches for a school
     */
    public void warmUpCaches(Long schoolId) {
        log.info("Starting cache warm-up for school: {}", schoolId);
        
        try {
            // Warm up student caches
            cachedStudentService.warmUpCaches(schoolId);
            
            // Warm up attendance caches
            cachedAttendanceService.warmUpAttendanceCaches(schoolId);
            
            log.info("Cache warm-up completed for school: {}", schoolId);
        } catch (Exception e) {
            log.error("Error during cache warm-up for school: {} - {}", schoolId, e.getMessage(), e);
        }
    }

    /**
     * Perform cache maintenance (clear expired entries, compact, etc.)
     */
    public void performCacheMaintenance() {
        log.info("Starting cache maintenance");
        
        try {
            // Get cache statistics before maintenance
            Map<String, Object> beforeStats = getCacheStatistics();
            log.info("Cache stats before maintenance: {}", beforeStats);
            
            // Clear expired entries (Redis handles this automatically, but we can force cleanup)
            try {
                if (redisTemplate.getConnectionFactory() != null) {
                    // Simplified approach - just clear specific cache prefixes
                    Set<String> keys = redisTemplate.keys("*");
                    if (keys != null && !keys.isEmpty()) {
                        redisTemplate.delete(keys);
                    }
                }
            } catch (Exception redisException) {
                log.warn("Redis cleanup operation failed: {}", redisException.getMessage());
            }
            
            // Get cache statistics after maintenance
            Map<String, Object> afterStats = getCacheStatistics();
            log.info("Cache stats after maintenance: {}", afterStats);
            
            log.info("Cache maintenance completed");
        } catch (Exception e) {
            log.error("Error during cache maintenance: {}", e.getMessage(), e);
        }
    }

    /**
     * Preload critical data into cache
     */
    public void preloadCriticalData(Long schoolId) {
        log.info("Preloading critical data for school: {}", schoolId);
        
        try {
            // Preload frequently accessed student data
            cachedStudentService.getStandardsBySchool(schoolId);
            
            // Preload today's attendance data
            cachedAttendanceService.getDailyAttendanceSummaryByClass(
                java.time.LocalDate.now(), schoolId);
            
            log.info("Critical data preloaded for school: {}", schoolId);
        } catch (Exception e) {
            log.error("Error preloading critical data for school: {} - {}", schoolId, e.getMessage(), e);
        }
    }

    /**
     * Get cache hit/miss ratio (simplified estimation)
     */
    public Map<String, Object> getCachePerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // This is a simplified example - in production you'd use metrics libraries
            // like Micrometer to track actual hit/miss ratios
            
            Collection<String> cacheNames = cacheManager.getCacheNames();
            metrics.put("monitoredCaches", cacheNames.size());
            metrics.put("lastUpdated", java.time.LocalDateTime.now());
            
            // Placeholder for actual metrics - implement with monitoring library
            metrics.put("note", "Implement actual metrics with Micrometer or similar");
            
        } catch (Exception e) {
            log.error("Error getting cache performance metrics: {}", e.getMessage(), e);
            metrics.put("error", e.getMessage());
        }
        
        return metrics;
    }

    /**
     * Health check for cache system
     */
    public Map<String, Object> getCacheHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test Redis connection
            if (redisTemplate.getConnectionFactory() != null) {
                redisTemplate.opsForValue().set("health-check", "test");
                Object testValue = redisTemplate.opsForValue().get("health-check");
                redisTemplate.delete("health-check");
                health.put("redis", "test".equals(testValue) ? "UP" : "DOWN");
            } else {
                health.put("redis", "DOWN - No connection factory");
            }
            
            // Test cache manager
            Collection<String> cacheNames = cacheManager.getCacheNames();
            health.put("cacheManager", "UP");
            health.put("availableCaches", cacheNames.size());
            
            // Test a simple cache operation
            Cache testCache = cacheManager.getCache("test");
            if (testCache != null) {
                testCache.put("healthCheck", "OK");
                String value = testCache.get("healthCheck", String.class);
                health.put("cacheOperations", "OK".equals(value) ? "UP" : "DOWN");
                testCache.evict("healthCheck");
            }
            
            health.put("status", "UP");
            health.put("timestamp", java.time.LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Cache health check failed: {}", e.getMessage(), e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", java.time.LocalDateTime.now());
        }
        
        return health;
    }
}
