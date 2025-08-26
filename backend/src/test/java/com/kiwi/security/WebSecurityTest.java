package com.kiwi.security;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.common.controllers.PingController;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(PingController.class)
@ActiveProfiles("test")
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class WebSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @WithMockUser
    public void ping_withAuthentication_returnsOk() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void publicPing_withoutAuthentication_returnsOk() throws Exception {
        mockMvc.perform(get("/api/public/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    
    @Test
    @WithMockUser(roles = "USER")
    public void userPing_withUserRole_returnsOk() throws Exception {
        mockMvc.perform(get("/api/user/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void userPing_withAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/api/user/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminPing_withAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void adminPing_withUserRole_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()); 
    }
}
