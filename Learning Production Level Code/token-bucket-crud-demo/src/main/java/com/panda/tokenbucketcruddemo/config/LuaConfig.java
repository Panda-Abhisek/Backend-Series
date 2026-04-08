package com.panda.tokenbucketcruddemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Configuration for Lua scripts used in rate limiting.
 * Loads the token_bucket.lua script for atomic Redis operations.
 * 
 * The token bucket algorithm requires atomic operations to prevent
 * race conditions. Lua scripts execute atomically in Redis.
 */
@Configuration
public class LuaConfig {

    /**
     * Creates the Redis script for token bucket rate limiting.
     * Script location: classpath:lua/token_bucket.lua
     * @return DefaultRedisScript configured for token bucket logic
     */
    @Bean
    public DefaultRedisScript<Long> tokenBucketScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/token_bucket.lua"));
        script.setResultType(Long.class);
        return script;
    }
}