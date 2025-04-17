package com.kiwi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.entity.UserSettings;
import com.kiwi.service.UserSettingsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(UserSettingsController.class)
public class UserSettingsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSettingsService userSettingsService;
    
    @Test
    public void createUserSettings_validInput_returnsCreatedStatus() throws Exception {
        UserSettings mockUserSettings = new UserSettings(
                1,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(mockUserSettings);
        
        mockMvc.perform(
                post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() throws Exception{
        Integer targetId = 1;
        String targetEmail = "finnthehuman@gmail.com";
        UserSettings mockUserSettings = new UserSettings(
                targetId,
                targetEmail,
                true,
                UserSettings.Theme.LIGHT
        );
        when(userSettingsService.getUserSettingsById(targetId)).thenReturn(Optional.of(mockUserSettings));
        
        mockMvc.perform(get(String.format("/api/settings/%d", targetId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(targetEmail));
    }

    @Test
    public void updateUserSettings_validInput_returnsUpdatedUserSettings() throws Exception {
        Integer targetId = 1;
        UserSettings mockUserSettings = new UserSettings(
                targetId,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);

        UserSettings userSettingsUpdate = new UserSettings(
                targetId,
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
        when(userSettingsService.updateUserSettings(userSettingsUpdate)).thenReturn(userSettingsUpdate);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(userSettingsUpdate);
        
        mockMvc.perform(
                put("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userSettingsUpdate.getEmail()));
    }

    @Test
    public void deleteUserSettings_validInput_returnsNoContent() throws Exception {
        Integer targetId = 1;
        UserSettings mockUserSettings = new UserSettings(
                targetId,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        when(userSettingsService.createUserSettings(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsService.getUserSettingsById(targetId)).thenReturn(Optional.of(mockUserSettings));
        
        mockMvc.perform(delete("/api/settings/{id}", targetId))
                .andExpect(status().isNoContent());
    }
}
