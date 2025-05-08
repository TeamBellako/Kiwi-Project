package com.kiwi.usersettings;

import com.kiwi.exception.GlobalExceptionHandler;
import com.kiwi.security.CustomUserDetailsService;
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

import java.util.Optional;

import static com.kiwi.usersettings.UserSettingsHTTPUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import static com.kiwi.usersettings.UserSettingsTestFactory.*;


@RunWith(SpringRunner.class)
@WebMvcTest(UserSettingsController.class)
@Import(GlobalExceptionHandler.class)
public class UsersSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private UserSettingsService userSettingsService;
    
    private final String baseAPIUrl = "/api/settings";

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getUserSettings_validInput_returnsUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsByEmail(validUserSettingsDTO().getEmail()))
            .thenReturn(Optional.of(validUserSettingsDTO()));
        
        mockMvc.perform(get(baseAPIUrl + "/me"))
            .andExpect(status().isOk())
            .andExpect(getUserSettingsResultMatcher(validUserSettingsDTO()));
    }

    @Test
    @WithMockUser(username = "bmolovesfootball.com")
    public void getUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.getUserSettingsByEmail(invalidUserSettingsDTO().getEmail()))
                .thenThrow(new UserSettingsInvalidException(""));

        mockMvc.perform(get(baseAPIUrl + "/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getUserSettings_nonExistingUser_returnsNotFound() throws Exception {
        mockMvc.perform(get(baseAPIUrl + "/me"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    public void getUserSettings_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(baseAPIUrl + "/me"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_validInput_returnsUpdatedUserSettingsDTO() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class)))
            .thenReturn(updatedUserSettingsDTO());
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, updatedUserSettingsDTO()))
            .andExpect(status().isOk())  
            .andExpect(getUserSettingsResultMatcher(updatedUserSettingsDTO()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class)))
            .thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl,invalidUserSettingsDTO()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, null))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_nonExistingUser_returnsNotFound() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class)))
            .thenThrow(new UserSettingsNotFoundException(validUserSettingsDTO().getEmail()));
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validUserSettingsDTO()))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserSettings_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validUserSettingsDTO()))
                .andExpect(status().isUnauthorized());
    }
}