package com.kiwi.security;

import com.kiwi.common.controllers.PingController;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(RateLimitFilter.class)
@RunWith(SpringRunner.class)
@WebMvcTest(PingController.class)
@AutoConfigureMockMvc
public class RateLimitFilterTests {
    
    public static final int TARGET_NUM_OF_REQUESTS = 201;
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void shouldEnforceRateLimitAfterTooManyRequests() throws Exception {
        for (int i = 0; i < TARGET_NUM_OF_REQUESTS; i++) {
            mockMvc.perform(get("/api/ping"))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isTooManyRequests());
    }
}

