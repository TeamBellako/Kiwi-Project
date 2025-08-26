package com.kiwi.settings;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.settings.controllers.SettingsController;
import com.kiwi.features.settings.controllers.SettingsService;
import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kiwi.settings.SettingsTestFactory.settingsDTO;
import static com.kiwi.settings.SettingsTestHTTPUtils.*;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

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

    private final UsersPersistence user = UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword());
    private final SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(user, settingsDTO());

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getSettings_validInput_returnsSettings() throws Exception {
        when(settingsService.getSettings(validUserDTO().getEmail()))
                .thenReturn(settingsPersistence);
        
        mockMvc.perform(get(baseAPIUrl))
                .andExpect(status().isOk());
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
        when(settingsService.updateSettings(validUserDTO().getEmail(), any(SettingsDTO.class)))
                .thenReturn(settingsDTO());
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, settingsDTO()))
                .andExpect(status().isOk())
                .andExpect(getSettingsResultMatcher(validUserDTO(), settingsDTO()));
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
        when(settingsService.updateSettings(validUserDTO().getEmail(), any(SettingsDTO.class)))
            .thenThrow(new SettingsNotFoundException(validUserDTO().getEmail()));
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, settingsDTO()))
            .andExpect(status().isNotFound());
    }
}