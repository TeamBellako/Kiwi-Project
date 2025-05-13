package com.kiwi.usersettings;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.usersettings.UserSettingsTestFactory.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsersSettingsServiceTest {

    private final UserSettingsRepository userSettingsRepository = Mockito.mock(UserSettingsRepository.class);
    private final UserSettingsService userSettingsService = new UserSettingsService(userSettingsRepository);
    
    private final UserSettings validUserSettings = validUserSettingsDTO().toDomainObject();
    private final UserSettings updatedUserSettings = updatedUserSettingsDTO().toDomainObject();

    @Test
    public void getUserSettings_validInput_returnsUserSettings() {
        when(userSettingsRepository.findByEmail(validUserSettings.getEmail().value())).thenReturn(Optional.of(validUserSettings));

        Optional<UserSettingsDTO> retrievedUserSettings = userSettingsService.getUserSettingsByEmail(validUserSettings.getEmail().value());

        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(validUserSettings.toDTO(), retrievedUserSettings.get());
        verify(userSettingsRepository, Mockito.times(1)).findByEmail(validUserSettings.getEmail().value());
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void getUserSettings_invalidInput_throwsUserSettingsInvalidException() {
        userSettingsService.getUserSettingsByEmail(invalidUserSettingsDTO().getEmail());
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void getUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.getUserSettingsByEmail(updatedUserSettingsDTO().getEmail());
    }

    @Test
    public void updateUserSettings_validInput_settingsUpdated() {
        when(userSettingsRepository.saveAndFlush(validUserSettings)).thenReturn(validUserSettings);
        when(userSettingsRepository.saveAndFlush(updatedUserSettings)).thenReturn(updatedUserSettings);
        when(userSettingsRepository.findByEmail(updatedUserSettings.getEmail().value())).thenReturn(Optional.of(validUserSettings));
        
        UserSettings newUserSettings = userSettingsService.updateUserSettings(updatedUserSettings.toDTO()).toDomainObject();

        assertEquals(updatedUserSettings, newUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).saveAndFlush(UsersSettingsServiceTest.this.updatedUserSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserSettings_invalidInput_throwsIllegalArgumentException() throws IllegalArgumentException {
        when(userSettingsRepository.existsByEmail(invalidUserSettingsDTO().getEmail())).thenReturn(true);
        
        userSettingsService.updateUserSettings(invalidUserSettingsDTO());
    }

    @Test(expected = NullPointerException.class)
    public void updateUserSettings_nullInput_throwsNullPointerException() {
        userSettingsService.updateUserSettings(null);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void updateUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.updateUserSettings(updatedUserSettings.toDTO());
    }
}
