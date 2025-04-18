package com.kiwi.usersettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserSettingsController.class)
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
    
    private final String baseAPIUrl = "/api/settings";

    
    @Test
    public void createUserSettings_validInput_returnsCreatedStatus() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        
        mockMvc.perform(post(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(mockUserSettings)))
                
        .andExpect(status().isCreated()) 
        .andExpect(getUserSettingsResultMatcher(mockUserSettings));
    }
    
    @Test
    public void createUserSettings_invalidInput_throwsUserSettingsInvalidException() {
        
    }
    
    @Test
    public void createUserSettings_nullInput_throwsIllegalArgumentException() {

    }

    @Test
    public void createUserSettings_userSettingsAlreadyExists_returnsConflict() {

    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));

        mockMvc.perform(get(baseAPIUrl + "/{id}", mockUserSettings.getId()))
                
        .andExpect(status().isOk()) 
        .andExpect(getUserSettingsResultMatcher(mockUserSettings));
    }

    @Test
    public void getUserSettingsById_invalidId_throwsUserSettingsInvalidException() {

    }

    @Test
    public void getUserSettingsById_invalidId_throwsUserSettingsNotFoundException() {

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
    public void updateUserSettings_invalidInput_throwsUserSettingsInvalidException() {

    }

    @Test
    public void updateUserSettings_nullInput_throwsIllegalArgumentException() {

    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {

    }

    @Test
    public void deleteUserSettings_validInput_returnsNoContent() throws Exception {
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsService.getUserSettingsById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));

        mockMvc.perform(delete(baseAPIUrl + "/{id}", mockUserSettings.getId()))
                
        .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserSettings_invalidId_throwsUserSettingsInvalidException() {

    }

    @Test
    public void deleteUserSettings_userNotFound_throwsUserSettingsNotFoundException() {

    }
    
    
    private String serializeUserSettingIntoJSON(UserSettings userSettings) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userSettings);
    }
    
    private ResultMatcher getUserSettingsResultMatcher(UserSettings expectedUserSettings) {
        return jsonPath("$.email").value(expectedUserSettings.getEmail());        
    }
}