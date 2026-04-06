package com.panda.ratelimitfixedwindowcounterdemo.services.ratelimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        log.info("Incoming request from IP: {}", ip);

        String key = "rate_limit:" + ip;
        log.info("Generated rate limit key: {}", key);

        if (!rateLimiterService.isAllowed(key)) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            log.warn("Rate limit exceeded for IP: {}, for path: {}", ip, request.getServletPath());
            return;
        }

        filterChain.doFilter(request, response);
    }
}