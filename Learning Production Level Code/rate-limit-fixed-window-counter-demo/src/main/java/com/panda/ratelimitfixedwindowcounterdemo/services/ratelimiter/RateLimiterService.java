package com.panda.ratelimitfixedwindowcounterdemo.services.ratelimiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    private final StringRedisTemplate stringRedisTemplate;

    private static final int LIMIT = 10;
    private static final int WINDOW_SECONDS = 60;

    public boolean isAllowed(String key) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if(count != null && count == 1) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }
        return count != null && count <= LIMIT;
    }
}
