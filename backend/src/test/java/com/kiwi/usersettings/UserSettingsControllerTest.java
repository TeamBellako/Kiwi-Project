package com.kiwi.usersettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.exception.GlobalExceptionHandler;
import jakarta.servlet.ServletException;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import static com.kiwi.usersettings.UserSettingsTestFactory.*;


@RunWith(SpringRunner.class)
@WebMvcTest(UserSettingsController.class)
@Import(GlobalExceptionHandler.class)
public class UserSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSettingsService userSettingsService;
    
    private final String baseAPIUrl = "/api/settings";

    
    @Test
    public void createUserSettings_validInput_returnsCreated() throws Exception {
        when(userSettingsService.createUserSettings(UserSettingsTestFactory.validUserSettings())).thenReturn(validUserSettings());
        
        mockMvc.perform(getPOSTRequestContent(validUserSettings()))
                
        .andExpect(status().isCreated()) 
        .andExpect(getUserSettingsResultMatcher(validUserSettings()));
    }

    @Test
    public void createUserSettings_invalidInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPOSTRequestContent(invalidUserSettings()))
        
        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPOSTRequestContent(null))

        .andExpect(status().isBadRequest());    
    }

    @Test
    public void createUserSettings_userSettingsAlreadyExists_returnsConflict() throws Exception {
        when(userSettingsService.createUserSettings(validUserSettings())).thenThrow(new UserSettingsConflictException(validUserSettings().getId()));

        mockMvc.perform(getPOSTRequestContent(validUserSettings()))

        .andExpect(status().isConflict()); 
    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsById(validUserSettings().getId())).thenReturn(Optional.of(validUserSettings()));

        mockMvc.perform(get(baseAPIUrl + "/{id}", validUserSettings().getId()))
                
        .andExpect(status().isOk()) 
        .andExpect(getUserSettingsResultMatcher(validUserSettings()));
    }

    @Test
    public void getUserSettingsById_invalidId_returnsBadRequest() throws Exception {
        when(userSettingsService.getUserSettingsById(invalidUserSettings().getId())).thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(get(baseAPIUrl + "/{id}", invalidUserSettings().getId()))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserSettingsById_userDoesNotExists_returnsNotFound() throws Exception {
        mockMvc.perform(get(baseAPIUrl + "/{id}", validUserSettings().getId()))

        .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserSettings_validInput_returnsUpdatedUserSettings() throws Exception {
        when(userSettingsService.createUserSettings(validUserSettings())).thenReturn(validUserSettings());
        when(userSettingsService.updateUserSettings(updatedUserSettings())).thenReturn(updatedUserSettings());
        
        mockMvc.perform(getPUTRequestContent(updatedUserSettings()))
        
        .andExpect(status().isOk())  
        .andExpect(getUserSettingsResultMatcher(updatedUserSettings()));
    }

    @Test
    public void updateUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettings.class))).thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(getPUTRequestContent(invalidUserSettings()))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPUTRequestContent(null))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_returnsNotFound() throws Exception {
        when(userSettingsService.updateUserSettings(validUserSettings())).thenThrow(new UserSettingsNotFoundException(validUserSettings().getId()));
        
        mockMvc.perform(getPUTRequestContent(validUserSettings()))

        .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserSettings_validInput_returnsNoContent() throws Exception {
        when(userSettingsService.createUserSettings(validUserSettings())).thenReturn(validUserSettings());
        when(userSettingsService.getUserSettingsById(validUserSettings().getId())).thenReturn(Optional.of(validUserSettings()));

        mockMvc.perform(delete(baseAPIUrl + "/{id}", validUserSettings().getId()))
                
        .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserSettings_invalidId_returnsBadRequest() throws Exception {
        doThrow(new UserSettingsInvalidException(""))
                .when(userSettingsService)
                .deleteUserSettings(invalidUserSettings().getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", invalidUserSettings().getId()))
                
        .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserSettings_userNotFound_returnsNotFound() throws Exception {
        doThrow(new UserSettingsNotFoundException(validUserSettings().getId()))
                .when(userSettingsService)
                .deleteUserSettings(validUserSettings().getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", validUserSettings().getId()))

        .andExpect(status().isNotFound());
    }

    
    private @NotNull MockHttpServletRequestBuilder getPOSTRequestContent(UserSettings userSettings) throws JsonProcessingException {
        return post(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettings));
    }

    private @NotNull MockHttpServletRequestBuilder getPUTRequestContent(UserSettings userSettings) throws JsonProcessingException {
        return put(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettings));
    }
    
    private String serializeUserSettingIntoJSON(UserSettings userSettings) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userSettings);
    }
    
    private @NotNull ResultMatcher getUserSettingsResultMatcher(@NotNull UserSettings expectedUserSettings) {
        return jsonPath("$.email").value(expectedUserSettings.getEmail());        
    }
}