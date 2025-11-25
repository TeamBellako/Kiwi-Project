package com.kiwi.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    @ConditionalOnProperty(
            name = "rate-limiter.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public RateLimitFilter rateLimitFilter(JwtUtils jwtUtils) {
        return new RateLimitFilter(jwtUtils);
    }
}
