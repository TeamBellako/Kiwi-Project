package com.kiwi.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    private final JwtUtils jwtUtils;
    
    @Autowired
    public RateLimitFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    
    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(200, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(200, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String key;
        
        String jwt = jwtUtils.parseJwt(request);
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            key = jwtUtils.getUsernameFromToken(jwt);
        } else {
            key = request.getRemoteAddr();
        }
        
        Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
        }
    }
}

