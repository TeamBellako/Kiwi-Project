package com.kiwi.usersettings;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.usersettings.UserSettingsTestFactory.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserSettingsServiceTest {

    private final UserSettingsRepository userSettingsRepository = Mockito.mock(UserSettingsRepository.class);
    private final UserSettingsService userSettingsService = new UserSettingsService(userSettingsRepository);


    @Test
    public void createUserSettings_validInput_settingsCreated() {
        when(userSettingsRepository.save(validUserSettings())).thenReturn(validUserSettings());

        UserSettings createdUserSettings = userSettingsService.createUserSettings(validUserSettings());

        assertEquals(validUserSettings(), createdUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).save(validUserSettings());
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void createUserSettings_invalidInput_throwsUserSettingsInvalidException() throws UserSettingsInvalidException {
        userSettingsService.createUserSettings(invalidUserSettings());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserSettings_nullInput_throwsIllegalArgumentException() {
        userSettingsService.createUserSettings(null);
    }

    @Test(expected = UserSettingsConflictException.class)
    public void createUserSettings_userSettingsAlreadyExists_throwsIllegalArgumentException() throws UserSettingsConflictException {
        when(userSettingsRepository.existsById(validUserSettings().getId())).thenReturn(true);

        userSettingsService.createUserSettings(validUserSettings());
        
        verify(userSettingsRepository, Mockito.times(1)).existsById(validUserSettings().getId());
        verify(userSettingsRepository, Mockito.times(1)).save(validUserSettings());
    }

    @Test(expected = IllegalStateException.class)
    public void createUserSettings_saveReturnsEmptyUserSettings_throwsIllegalStateException() {
        when(userSettingsRepository.save(validUserSettings())).thenReturn(new UserSettings());
        
        userSettingsService.createUserSettings(validUserSettings());
    }

    @Test(expected = RuntimeException.class)
    public void createUserSettings_repositoryFails_throwsRuntimeException() {
        when(userSettingsRepository.save(validUserSettings())).thenThrow(new RuntimeException());
        
        userSettingsService.createUserSettings(validUserSettings());
    }

    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() {
        when(userSettingsRepository.findById(validUserSettings().getId())).thenReturn(Optional.of(validUserSettings()));

        Optional<UserSettings> retrievedUserSettings = userSettingsService.getUserSettingsById(validUserSettings().getId());

        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(validUserSettings(), retrievedUserSettings.get());
        verify(userSettingsRepository, Mockito.times(1)).findById(validUserSettings().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserSettingsById_invalidInput_throwsIllegalArgumentException() {
        userSettingsService.getUserSettingsById(-1);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void getUserSettingsById_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.getUserSettingsById(1);
    }

    @Test
    public void updateUserSettings_validInput_settingsUpdated() {
        when(userSettingsRepository.save(validUserSettings())).thenReturn(validUserSettings());
        when(userSettingsRepository.existsById(validUserSettings().getId())).thenReturn(true);
        when(userSettingsRepository.save(updatedUserSettings())).thenReturn(updatedUserSettings());
        
        UserSettings updatedUserSettings = userSettingsService.updateUserSettings(updatedUserSettings());

        assertEquals(updatedUserSettings(), updatedUserSettings);
        assertNotEquals(validUserSettings(), updatedUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).existsById(validUserSettings().getId());
        verify(userSettingsRepository, Mockito.times(1)).save(updatedUserSettings());
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void updateUserSettings_invalidInput_throwsUserSettingsInvalidException() throws UserSettingsInvalidException {
        when(userSettingsRepository.save(validUserSettings())).thenReturn(validUserSettings());
        when(userSettingsRepository.existsById(validUserSettings().getId())).thenReturn(true);

        invalidUserSettings().setId(validUserSettings().getId());
        userSettingsService.updateUserSettings(invalidUserSettings());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserSettings_nullInput_throwsIllegalArgumentException() {
        userSettingsService.updateUserSettings(null);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void updateUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.updateUserSettings(updatedUserSettings());
    }

    @Test
    public void deleteUserSettings_validInput_settingsDeleted() {
        when(userSettingsRepository.save(validUserSettings())).thenReturn(validUserSettings());
        when(userSettingsRepository.existsById(validUserSettings().getId())).thenReturn(true);

        userSettingsService.deleteUserSettings(validUserSettings().getId());
        
        verify(userSettingsRepository, Mockito.times(1)).deleteById(validUserSettings().getId());
        verify(userSettingsRepository, Mockito.times(1)).existsById(validUserSettings().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteUserSettings_invalidInput_throwsIllegalArgumentException() {
        userSettingsService.deleteUserSettings(-1);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void deleteUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.deleteUserSettings(1);
    }
}
