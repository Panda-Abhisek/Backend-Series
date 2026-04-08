package com.panda.tokenbucketcruddemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis configuration for the application.
 * Provides StringRedisTemplate for Redis operations.
 * 
 * Used by:
 * - TokenBucketService for rate limiting
 * 
 * Configure Redis connection in application.yaml:
 *   spring.data.redis.host: localhost
 *   spring.data.redis.port: 6379
 */
@Configuration
public class RedisConfig {

    /**
     * Creates StringRedisTemplate for string-based Redis operations.
     * Key-value pairs are stored as strings.
     * @param connectionFactory Redis connection factory (auto-configured)
     * @return StringRedisTemplate instance
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}