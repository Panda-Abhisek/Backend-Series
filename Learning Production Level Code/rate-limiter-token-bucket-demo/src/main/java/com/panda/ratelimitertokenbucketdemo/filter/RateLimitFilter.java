package com.panda.ratelimitertokenbucketdemo.filter;

import com.panda.ratelimitertokenbucketdemo.ratelimit.TokenBucketService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final TokenBucketService tokenBucketService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String key = "token_bucket:" + ip;

        // this filter pass the key to the token bucket service to check if the request is allowed or not
        // if not, it should return a 429 Too Many Requests response
        if(!tokenBucketService.allowRequest(key)) {
            response.setStatus(429);
            response.getWriter().write("Too Many Requests");
            return;
        }

        // if yes, it will forward the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
