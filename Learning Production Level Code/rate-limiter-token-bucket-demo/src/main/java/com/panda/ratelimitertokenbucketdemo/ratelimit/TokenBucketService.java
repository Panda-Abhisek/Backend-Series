package com.panda.ratelimitertokenbucketdemo.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TokenBucketService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    private static final int CAPACITY = 10;
    private static final int REFILL_RATE = 1;

    public boolean allowRequest(String key) {
        long now = Instant.now().getEpochSecond();
        // get the token bucket from redis by the key
        // if the token bucket does not exist, create a new one with the initial tokens and the last refill time
        // if the token bucket exists, calculate the number of tokens to add based on the time elapsed since the last refill time and the refill rate
        // if the number of tokens in the bucket is greater than 0, allow the request and decrease the token count by 1, and update the last refill time
        // if the number of tokens in the bucket is 0, reject the request
        //return true; // this is just a placeholder, you should implement the actual logic to interact with Redis and manage the token bucket

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
