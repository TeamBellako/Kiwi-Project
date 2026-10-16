package com.kiwi.users;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.features.users.controllers.UsersController.LanguageController;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(LanguageController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import(WebSecurityConfig.class)
public class UserLanguageControllerTests {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private UsersService usersService;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @WithMockUser(username = "person@example.com")
    public void updateLanguage_usesAuthenticatedAccount() throws Exception {
        mockMvc.perform(put("/api/user/language")
                        .contentType("application/json")
                        .content("{\"language\":\"es\"}"))
                .andExpect(status().isNoContent());

        verify(usersService).updateUserLanguage("person@example.com", "es");
    }
}