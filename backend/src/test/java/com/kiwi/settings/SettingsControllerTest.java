package com.kiwi.settings;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.settings.*;
import com.kiwi.utils.GlobalExceptionHandler;
import com.kiwi.security.*;
import com.kiwi.features.users.CustomUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.kiwi.settings.SettingsTestHTTPUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import static com.kiwi.settings.SettingsTestFactory.*;

@RunWith(SpringRunner.class)
@WebMvcTest(SettingsController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;
    
    @MockitoBean
    private SettingsService settingsService;
    
    private final String baseAPIUrl = "/api/user/settings";
    
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getSettings_validInput_returnsSettings() throws Exception {
        when(settingsService.getSettingsByEmail(validSettingsDTO().getEmail()))
                .thenReturn(Optional.of(validSettingsDTO()));
        
        mockMvc.perform(get(baseAPIUrl))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "football")
    public void getSettings_invalidInput_returnsBadRequest() throws Exception {
        when(settingsService.getSettingsByEmail(invalidSettingsDTO().getEmail()))
                .thenThrow(new SettingsInvalidException(""));

        mockMvc.perform(get(baseAPIUrl))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getSettings_nonExistingUser_returnsNotFound() throws Exception {
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_validInput_returnsUpdatedSettingsDTO() throws Exception {
        when(settingsService.updateSettings(any(SettingsDTO.class)))
                .thenReturn(validSettingsDTO());
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validSettingsDTO()))
                .andExpect(status().isOk())
                .andExpect(getSettingsResultMatcher(validSettingsDTO()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_invalidInput_returnsBadRequest() throws Exception {
        when(settingsService.updateSettings(any(SettingsDTO.class)))
            .thenThrow(new SettingsInvalidException(""));
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, invalidSettingsDTO()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, null))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_nonExistingUser_returnsNotFound() throws Exception {
        when(settingsService.updateSettings(any(SettingsDTO.class)))
            .thenThrow(new SettingsNotFoundException(validSettingsDTO().getEmail()));
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validSettingsDTO()))
            .andExpect(status().isNotFound());
    }
}