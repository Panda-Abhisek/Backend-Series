package com.panda.lp.services;

import com.panda.lp.config.RedisKeyGenerator;
import com.panda.lp.payloads.PaginatedResponse;
import com.panda.lp.payloads.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRedisCacheService {
    // cache configuration - TTL, eviction policies, etc. can be set in application.properties or application.yml
    private static final long CACHE_TTL_MINUTES = 10; // 10 minutes
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyGenerator redisKeyGenerator;

    // get cached offset-based response
    public PaginatedResponse<ProductResponse> getCachedOffsetResponse(
            int page,
            int size,
            String sortBy,
            String sortDir,

            String title,
            Boolean live,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        String cacheKey = redisKeyGenerator.generateOffsetCacheKey(page, size, sortBy, sortDir, title, live, minPrice, maxPrice);
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey); // this will try to get the cached response from Redis using the generated key
            if (cachedData != null) {
                log.info("Cache hit for key: {}", cacheKey);
                return (PaginatedResponse<ProductResponse>) cachedData; // if cache hit then return the cached response
            }
            log.info("Cache miss for key: {}", cacheKey);
            return null; // if cache miss then return null and we can fetch data from DB and then cache it
        } catch (Exception e) {
            log.error("Error while fetching cached response for key: {}. Error: {}", cacheKey, e.getMessage());
            return null; // in case of any error while fetching from cache, we can return null and fetch data from DB
        }
    }

    // cache offset-based response
    public void cacheOffsetResponse(
            PaginatedResponse<ProductResponse> response,
            int page,
            int size,
            String sortBy,
            String sortDir,

            String title,
            Boolean live,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        String cacheKey = redisKeyGenerator.generateOffsetCacheKey(page, size, sortBy, sortDir, title, live, minPrice, maxPrice);
        try {
            redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL_MINUTES, TimeUnit.MINUTES); // this will store the response in Redis with the generated key
            log.info("Cache saved for key: {}", cacheKey);
        } catch (Exception e) {
            log.error("Error while caching response for key: {}. Error: {}", cacheKey, e.getMessage());
        }
    }

    public void evictOffsetCache() {
        String pattern = redisKeyGenerator.generateOffsetCachePattern(); // this will generate the pattern for all offset-based cache keys
        try {
            redisTemplate.keys(pattern).forEach(key -> {
                redisTemplate.delete(key); // this will delete all keys matching the pattern
                log.info("Cache evicted for key: {}", key);
            });
        } catch (Exception e) {
            log.error("Error while evicting cache with pattern: {}. Error: {}", pattern, e.getMessage());
        }
    }
}
