package com.panda.tokenbucketcruddemo.filter;

import com.panda.tokenbucketcruddemo.ratelimit.TokenBucketService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate limiting filter using token bucket algorithm.
 * 
 * Applied to all incoming requests before they reach controllers.
 * Identifies users by:
 * 1. X-User-ID header (preferred, for authenticated users)
 * 2. Fallback to client IP address
 * 
 * Rate limit exceeded:
 * - Returns HTTP 429 (Too Many Requests)
 * - JSON error response with message
 * 
 * Token bucket settings (TokenBucketService):
 * - Capacity: 10 tokens
 * - Refill rate: 1 token/second
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final TokenBucketService tokenBucketService;

    /**
     * Processes each request to check rate limit.
     * Extracts user identifier, checks token bucket, allows/rejects request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract user identifier: prefer X-User-ID header, fallback to IP
        String userId = request.getHeader("X-User-ID");
        
        if (userId == null || userId.isBlank()) {
            userId = request.getRemoteAddr();
        }
        
        // Redis key for this user's token bucket
        String key = "token_bucket:" + userId;

        // Check if request is allowed
        if (!tokenBucketService.allowRequest(key)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
            return;
        }

        // Request allowed - continue to next filter/controller
        filterChain.doFilter(request, response);
    }
}