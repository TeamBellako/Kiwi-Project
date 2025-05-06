package com.kiwi.usersettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.exception.GlobalExceptionHandler;
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

import static com.kiwi.usersettings.UserSettingsController.testingUserSettingsId;
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
        when(userSettingsService.createUserSettings(any(UserSettingsDTO.class))).thenReturn(validUserSettingsDTO());
        
        mockMvc.perform(getPOSTRequestContent(validUserSettingsDTO()))
                
        .andExpect(status().isCreated()) 
        .andExpect(getUserSettingsResultMatcher(validUserSettingsDTO()));
    }

    @Test
    public void createUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.createUserSettings(any(UserSettingsDTO.class))).thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(getPOSTRequestContent(invalidUserSettingsDTO()))
        
        .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPOSTRequestContent(null))

        .andExpect(status().isBadRequest());    
    }

    @Test
    public void createUserSettings_userSettingsAlreadyExists_returnsConflict() throws Exception {
        when(userSettingsService.createUserSettings(any(UserSettingsDTO.class)))
                .thenThrow(new UserSettingsConflictException(validUserSettingsDTO().getId()));

        mockMvc.perform(getPOSTRequestContent(validUserSettingsDTO()))

        .andExpect(status().isConflict()); 
    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsById(validUserSettingsDTO().getId())).thenReturn(Optional.of(validUserSettingsDTO()));

        mockMvc.perform(get(baseAPIUrl + "/{id}", validUserSettingsDTO().getId()))
                
        .andExpect(status().isOk()) 
        .andExpect(getUserSettingsResultMatcher(validUserSettingsDTO()));
    }

    @Test
    public void getUserSettingsById_invalidId_returnsBadRequest() throws Exception {
        when(userSettingsService.getUserSettingsById(invalidUserSettingsDTO().getId())).thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(get(baseAPIUrl + "/{id}", invalidUserSettingsDTO().getId()))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserSettingsById_userDoesNotExists_returnsNotFound() throws Exception {
        mockMvc.perform(get(baseAPIUrl + "/{id}", validUserSettingsDTO().getId()))

        .andExpect(status().isNotFound());
    }

    @Test
    public void getMyUserSettings_userExists_returnsMyUserSettings() throws Exception {
        when(userSettingsService.getUserSettingsById(testingUserSettingsId)).thenReturn(Optional.of(validUserSettingsDTO()));
        
        mockMvc.perform(get(baseAPIUrl + "/me", testingUserSettingsId))
        
        .andExpect(status().isOk())
        .andExpect(getUserSettingsResultMatcher(validUserSettingsDTO()));
    }

    @Test
    public void updateUserSettings_validInput_returnsUpdatedUserSettingsDTO() throws Exception {
        when(userSettingsService.createUserSettings(any(UserSettingsDTO.class))).thenReturn(validUserSettingsDTO());
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class))).thenReturn(updatedUserSettingsDTO());
        
        mockMvc.perform(getPUTRequestContent(updatedUserSettingsDTO()))
        
        .andExpect(status().isOk())  
        .andExpect(getUserSettingsResultMatcher(updatedUserSettingsDTO()));
    }

    @Test
    public void updateUserSettings_invalidInput_returnsBadRequest() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class))).thenThrow(new UserSettingsInvalidException(""));
        
        mockMvc.perform(getPUTRequestContent(invalidUserSettingsDTO()))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_nullInput_returnsBadRequest() throws Exception {
        mockMvc.perform(getPUTRequestContent(null))

        .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_returnsNotFound() throws Exception {
        when(userSettingsService.updateUserSettings(any(UserSettingsDTO.class)))
                .thenThrow(new UserSettingsNotFoundException(validUserSettingsDTO().getId()));
        
        mockMvc.perform(getPUTRequestContent(validUserSettingsDTO()))

        .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserSettings_validInput_returnsNoContent() throws Exception {
        when(userSettingsService.createUserSettings(any(UserSettingsDTO.class))).thenReturn(validUserSettingsDTO());
        when(userSettingsService.getUserSettingsById(validUserSettingsDTO().getId())).thenReturn(Optional.of(validUserSettingsDTO()));

        mockMvc.perform(delete(baseAPIUrl + "/{id}", validUserSettingsDTO().getId()))
                
        .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserSettings_invalidId_returnsBadRequest() throws Exception {
        doThrow(new UserSettingsInvalidException(""))
                .when(userSettingsService)
                .deleteUserSettings(invalidUserSettingsDTO().getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", invalidUserSettingsDTO().getId()))
                
        .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUserSettings_userNotFound_returnsNotFound() throws Exception {
        doThrow(new UserSettingsNotFoundException(validUserSettingsDTO().getId()))
                .when(userSettingsService)
                .deleteUserSettings(validUserSettingsDTO().getId());
        
        mockMvc.perform(delete(baseAPIUrl + "/{id}", validUserSettingsDTO().getId()))

        .andExpect(status().isNotFound());
    }

    
    private @NotNull MockHttpServletRequestBuilder getPOSTRequestContent(UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        return post(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettingsDTO));
    }

    private @NotNull MockHttpServletRequestBuilder getPUTRequestContent(UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        return put(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettingsDTO));
    }
    
    private String serializeUserSettingIntoJSON(UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userSettingsDTO);
    }
    
    private @NotNull ResultMatcher getUserSettingsResultMatcher(@NotNull UserSettingsDTO userSettingsDTO) {
        return jsonPath("$.email").value(userSettingsDTO.getEmail());        
    }
}