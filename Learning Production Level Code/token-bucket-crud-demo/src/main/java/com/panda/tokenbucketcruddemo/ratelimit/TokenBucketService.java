package com.panda.tokenbucketcruddemo.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

/**
 * Token bucket rate limiting service.
 * 
 * Algorithm:
 * 1. Each user has a bucket with fixed capacity (tokens)
 * 2. Tokens are added at a constant rate (refill_rate per second)
 * 3. Each request consumes 1 token
 * 4. If bucket is empty, request is rejected (HTTP 429)
 * 
 * Implementation uses Redis Lua script for atomic operations,
 * preventing race conditions in distributed environments.
 * 
 * Configuration (hardcoded for demo):
 * - capacity: Maximum tokens in bucket (10)
 * - refill_rate: Tokens added per second (1)
 */
@Service
@RequiredArgsConstructor
public class TokenBucketService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    /** Maximum tokens allowed in bucket */
    private static final int CAPACITY = 10;
    /** Tokens added per second */
    private static final int REFILL_RATE = 1;

    /**
     * Checks if request is allowed based on token bucket algorithm.
     * @param key Redis key for the user's token bucket (e.g., "token_bucket:user123")
     * @return true if request allowed, false if rate limit exceeded
     */
    public boolean allowRequest(String key) {
        long now = Instant.now().getEpochSecond();

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(key),
                String.valueOf(CAPACITY),
                String.valueOf(REFILL_RATE),
                String.valueOf(now),
                "1"
        );

        return result != null && result == 1;
    }
}