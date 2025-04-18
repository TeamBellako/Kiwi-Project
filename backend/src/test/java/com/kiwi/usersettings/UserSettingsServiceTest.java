package com.kiwi.usersettings;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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


    @Test
    public void createUserSettings_validInput_settingsCreated() {
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);

        UserSettings createdUserSettings = userSettingsService.createUserSettings(mockUserSettings);

        assertEquals(mockUserSettings, createdUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).save(mockUserSettings);
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void createUserSettings_invalidInput_throwsInvalidUserSettingsException() throws UserSettingsInvalidException {
        userSettingsService.createUserSettings(invalidUserSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserSettings_nullInput_throwsIllegalArgumentException() {
        userSettingsService.createUserSettings(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createUserSettings_userSettingsAlreadyExists_throwsIllegalArgumentException() throws IllegalArgumentException {
        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(true);

        userSettingsService.createUserSettings(mockUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).existsById(mockUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).save(mockUserSettings);
    }

    @Test(expected = IllegalStateException.class)
    public void createUserSettings_saveReturnsEmptyUserSettings_throwsIllegalStateException() {
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(new UserSettings());
        userSettingsService.createUserSettings(mockUserSettings);
    }

    @Test(expected = RuntimeException.class)
    public void createUserSettings_repositoryFails_throwsRuntimeException() {
        when(userSettingsRepository.save(mockUserSettings)).thenThrow(new RuntimeException());
        userSettingsService.createUserSettings(mockUserSettings);
    }

    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() {
        when(userSettingsRepository.findById(mockUserSettings.getId())).thenReturn(Optional.of(mockUserSettings));

        Optional<UserSettings> retrievedUserSettings = userSettingsService.getUserSettingsById(mockUserSettings.getId());

        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(mockUserSettings, retrievedUserSettings.get());
        verify(userSettingsRepository, Mockito.times(1)).findById(mockUserSettings.getId());
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
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(true);

        when(userSettingsRepository.save(userSettingsUpdate)).thenReturn(userSettingsUpdate);
        UserSettings updatedUserSettings = userSettingsService.updateUserSettings(userSettingsUpdate);

        assertEquals(userSettingsUpdate, updatedUserSettings);
        assertNotEquals(mockUserSettings, updatedUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).existsById(mockUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).save(userSettingsUpdate);
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void updateUserSettings_invalidInput_throwsInvalidUserSettingsException() throws UserSettingsInvalidException {
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(true);

        invalidUserSettings.setId(mockUserSettings.getId());
        userSettingsService.updateUserSettings(invalidUserSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserSettings_nullInput_throwsIllegalArgumentException() {
        userSettingsService.updateUserSettings(null);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void updateUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.updateUserSettings(userSettingsUpdate);
    }

    @Test
    public void deleteUserSettings_validInput_settingsDeleted() {
        when(userSettingsRepository.save(mockUserSettings)).thenReturn(mockUserSettings);
        when(userSettingsRepository.existsById(mockUserSettings.getId())).thenReturn(true);

        userSettingsService.deleteUserSettings(mockUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).deleteById(mockUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).existsById(mockUserSettings.getId());
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
