package com.kiwi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimitFilterTests {
    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    private final RateLimitFilter filter = new RateLimitFilter(jwtUtils);
    private final AtomicInteger allowedRequests = new AtomicInteger();
    private final FilterChain countingChain = (request, response) -> allowedRequests.incrementAndGet();

    @BeforeEach
    void setUp() {
        when(jwtUtils.parseJwt(any(HttpServletRequest.class))).thenReturn(null);
    }

    @Test
    void appliesLimitPerRemoteAddress() throws Exception {
        for (int i = 0; i < 200; i++) {
            filter.doFilter(requestFrom("192.0.2.1"), new MockHttpServletResponse(), countingChain);
        }

        MockHttpServletResponse limitedResponse = new MockHttpServletResponse();
        filter.doFilter(requestFrom("192.0.2.1"), limitedResponse, countingChain);

        assertEquals(429, limitedResponse.getStatus());
        assertEquals(200, allowedRequests.get());

        MockHttpServletResponse otherAddressResponse = new MockHttpServletResponse();
        filter.doFilter(requestFrom("192.0.2.2"), otherAddressResponse, countingChain);

        assertEquals(200, otherAddressResponse.getStatus());
        assertEquals(201, allowedRequests.get());
    }

    private MockHttpServletRequest requestFrom(String remoteAddress) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddress);
        return request;
    }
}
