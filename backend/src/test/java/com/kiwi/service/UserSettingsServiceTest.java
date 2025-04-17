package com.kiwi.service;

import com.kiwi.entity.UserSettings;
import com.kiwi.repository.UserSettingsRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserSettingsServiceTest {
    
    private final UserSettingsRepository userSettingsRepository = Mockito.mock(UserSettingsRepository.class);
    private final UserSettingsService userSettingsService = new UserSettingsService(userSettingsRepository);

    private final UserSettings mockUserSettings = new UserSettings(
            1,
            "finnthehuman@gmail.com",
            true,
            UserSettings.Theme.LIGHT
    );
    
    @Test
    public void createUserSettings_validInput_settingsCreated() {
        saveMockUserSettings();
        
        UserSettings createdUserSettings = userSettingsService.createUserSettings(mockUserSettings);
        
        assertEquals(mockUserSettings, createdUserSettings);
    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() {
        when(userSettingsRepository.findById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));
        
        Optional<UserSettings> retrievedUserSettings = userSettingsService.getUserSettingsById(mockUserSettings.getId());
        
        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(mockUserSettings, retrievedUserSettings.get());
    }
    
    @Test
    public void updateUserSettings_validInput_settingsUpdated() {
        saveMockUserSettings();
        
        UserSettings userSettingsUpdate = new UserSettings(
                mockUserSettings.getId(),
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
        when(userSettingsRepository.save(userSettingsUpdate)).thenReturn(userSettingsUpdate);
        UserSettings updatedUserSettings = userSettingsService.updateUserSettings(userSettingsUpdate);
        
        assertEquals(userSettingsUpdate, updatedUserSettings);
        assertNotEquals(mockUserSettings, updatedUserSettings);
    }

    @Test
    public void deleteUserSettings_validInput_settingsDeleted() {
        saveMockUserSettings();

        userSettingsService.deleteUserSettings(mockUserSettings.getId());
        assertTrue(userSettingsRepository.existsById(mockUserSettings.getId()));

        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(false);
        assertFalse(userSettingsRepository.existsById(mockUserSettings.getId()));
    }
    
    private void saveMockUserSettings() {
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(true);
    }
}