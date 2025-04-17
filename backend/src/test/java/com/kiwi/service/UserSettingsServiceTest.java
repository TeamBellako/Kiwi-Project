package com.kiwi.service;

import com.kiwi.entity.UserSettings;
import com.kiwi.repository.UserSettingsRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserSettingsServiceTest {
    private final UserSettingsRepository userSettingsRepository = Mockito.mock(UserSettingsRepository.class);
    private final UserSettingsService userSettingsService = new UserSettingsService(userSettingsRepository);
    
    @Test
    public void createUserSettings_validInput_settingsCreated() {
        UserSettings mockUserSettings = new UserSettings(
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        Mockito.when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        
        UserSettings createdUserSettings = userSettingsService.createUserSettings(mockUserSettings);

        assertEquals(mockUserSettings, createdUserSettings);
    }
    
    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() {
        Integer testId = 1;
        
        UserSettings mockUserSettings = new UserSettings(
                testId,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        Mockito.when(userSettingsRepository.findById(testId)).thenReturn(Optional.of(mockUserSettings));
        
        Optional<UserSettings> retrievedUserSettings = userSettingsService.getUserSettingsById(testId);
        
        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(mockUserSettings, retrievedUserSettings.get());
    }
    
    @Test
    public void updateUserSettings_validInput_settingsUpdated() {
        Integer testId = 1;
        
        UserSettings mockUserSettings = new UserSettings(
                testId,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        Mockito.when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);

        UserSettings userSettingsUpdate = new UserSettings(
                testId,
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
        userSettingsService.updateUserSettings(userSettingsUpdate);
        
        UserSettings updatedUserSettings = new UserSettings();
        Mockito.when(userSettingsRepository.findById(testId)).thenReturn(Optional.of(updatedUserSettings));
        
        assertEquals(userSettingsUpdate, updatedUserSettings);
        assertNotEquals(mockUserSettings, updatedUserSettings);
    }
    
    @Test
    public void deleteUserSettings_validInput_settingsDeleted() {
        Integer testId = 1;

        UserSettings mockUserSettings = new UserSettings(
                testId,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
        Mockito.when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        
        assertTrue(userSettingsRepository.existsById(testId));
        
        userSettingsService.deleteUserSettings(testId);
        
        assertFalse(userSettingsRepository.existsById(testId));
    }
}