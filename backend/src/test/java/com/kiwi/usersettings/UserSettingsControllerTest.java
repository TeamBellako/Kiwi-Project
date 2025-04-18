package com.kiwi.usersettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.exception.GlobalExceptionHandler;
import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
import jakarta.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserSettingsController.class)
@Import(GlobalExceptionHandler.class)
public class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSettingsService userSettingsService;
    
    private final UserSettings mockUserSettings = new UserSettings(
            1,
            "finnthehuman@gmail.com",
            true,
            UserSettings.Theme.LIGHT
    );
    private final UserSettings userSettingsUpdate = new UserSettings(
            mockUserSettings.getId(),
            "jakethedog@gmail.com",
            false,
            UserSettings.Theme.DARK
    );
    private final UserSettings invalidUserSettings = new UserSettings(
            -1,
            "bmotherobot.com",
            false,
            UserSettings.Theme.DARK
    );
    
    private final String baseAPIUrl = "/api/settings";

    
    @Test
    public void createUserSettings_validInput_returnsCreated() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        
        mockMvc.perform(post(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(mockUserSettings)))
                
        .andExpect(status().isCreated()) 
        .andExpect(getUserSettingsResultMatcher(mockUserSettings));
    }
    
    @Test(expected = ServletException.class)
    public void createUserSettings_invalidInput_returnsBadRequest() throws Exception {
        mockMvc.perform(post(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(serializeUserSettingIntoJSON(invalidUserSettings)));
    }
    
    @Test
    public void createUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(post(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(""))

        .andExpect(status().isBadRequest());    
    }

    @Test
    public void createUserSettings_userSettingsAlreadyExists_returnsConflict() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenThrow(new UserSettingsConflictException(mockUserSettings.getId()));

        mockMvc.perform(post(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(serializeUserSettingIntoJSON(mockUserSettings)))

        .andExpect(status().isConflict()); 
    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));

        mockMvc.perform(get(baseAPIUrl + "/{id}", mockUserSettings.getId()))
                
        .andExpect(status().isOk()) 
        .andExpect(getUserSettingsResultMatcher(mockUserSettings));
    }

    @Test
    public void getUserSettingsById_invalidId_returnsBadRequest() throws Exception {
        when(userSettingsService.getUserSettingsById(invalidUserSettings.getId())).thenThrow(new UserSettingsInvalidException());
        
        mockMvc.perform(get(baseAPIUrl + "/{id}", invalidUserSettings.getId()))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserSettingsById_userDoesNotExists_returnsNotFound() throws Exception {
        mockMvc.perform(get(baseAPIUrl + "/{id}", mockUserSettings.getId()))

        .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserSettings_validInput_returnsUpdatedUserSettings() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsService.updateUserSettings(userSettingsUpdate)).thenReturn(userSettingsUpdate);
        
        mockMvc.perform(put(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettingsUpdate)))
        
        .andExpect(status().isOk())  
        .andExpect(getUserSettingsResultMatcher(userSettingsUpdate));
    }

    @Test
    public void updateUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettings.class))).thenThrow(new UserSettingsInvalidException());
        
        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(serializeUserSettingIntoJSON(invalidUserSettings)))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(""))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_returnsNotFound() throws Exception {
        when(userSettingsService.updateUserSettings(mockUserSettings)).thenThrow(new UserSettingsNotFoundException(mockUserSettings.getId()));
        
        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(serializeUserSettingIntoJSON(mockUserSettings)))

        .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserSettings_validInput_returnsNoContent() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsService.getUserSettingsById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));

        mockMvc.perform(delete(baseAPIUrl + "/{id}", mockUserSettings.getId()))
                
        .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserSettings_invalidId_returnsBadRequest() throws Exception {
        doThrow(new UserSettingsInvalidException())
                .when(userSettingsService)
                .deleteUserSettings(invalidUserSettings.getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", invalidUserSettings.getId()))
                
        .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserSettings_userNotFound_returnsNotFound() throws Exception {
        doThrow(new UserSettingsNotFoundException(mockUserSettings.getId()))
                .when(userSettingsService)
                .deleteUserSettings(mockUserSettings.getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", mockUserSettings.getId()))

        .andExpect(status().isNotFound());
    }
    
    
    private String serializeUserSettingIntoJSON(UserSettings userSettings) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userSettings);
    }
    
    private ResultMatcher getUserSettingsResultMatcher(UserSettings expectedUserSettings) {
        return jsonPath("$.email").value(expectedUserSettings.getEmail());        
    }
}