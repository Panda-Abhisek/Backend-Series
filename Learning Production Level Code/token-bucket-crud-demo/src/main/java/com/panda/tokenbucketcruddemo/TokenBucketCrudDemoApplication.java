package com.panda.tokenbucketcruddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Token Bucket Rate Limiting with CRUD Demo.
 * 
 * Demonstrates:
 * - Token bucket algorithm for rate limiting using Redis
 * - RESTful CRUD operations for User entity
 * - Per-user rate limiting via X-User-ID header
 * 
 * Run: ./mvnw spring-boot:run
 * Requires: Redis on localhost:6379
 */
@SpringBootApplication
public class TokenBucketCrudDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TokenBucketCrudDemoApplication.class, args);
    }
}