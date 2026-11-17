package com.kiwi.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);
    private static final Set<String> HEALTH_CHECK_PATHS = Set.of(
            "/api/ping",
            "/api/public/ping",
            "/api/user/ping",
            "/api/admin/ping"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return HEALTH_CHECK_PATHS.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.nanoTime();
        String failure = "none";

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            failure = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            Object handlerPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String path = handlerPattern instanceof String
                    ? (String) handlerPattern
                    : request.getRequestURI();
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;

            logger.info("HTTP {} {} -> status={}, contentType={}, durationMs={}, failure={}",
                    request.getMethod(),
                    path,
                    response.getStatus(),
                    response.getContentType(),
                    durationMs,
                    failure);
        }
    }
}
