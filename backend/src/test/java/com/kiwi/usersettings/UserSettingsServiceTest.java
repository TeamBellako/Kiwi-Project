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
    
    private final UserSettings validUserSettings = validUserSettingsDTO().toDomainObject();
    private final UserSettings updatedUserSettings = updatedUserSettingsDTO().toDomainObject();

    @Test
    public void createUserSettings_validInput_settingsCreated() {
        when(userSettingsRepository.save(validUserSettings)).thenReturn(validUserSettings);

        UserSettings createdUserSettings = userSettingsService.createUserSettings(validUserSettings.toDTO()).toDomainObject();

        assertEquals(validUserSettings, createdUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).save(validUserSettings);
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void createUserSettings_invalidInput_throwsUserSettingsInvalidException() throws UserSettingsInvalidException {
        userSettingsService.createUserSettings(invalidUserSettingsDTO());
    }

    @Test(expected = NullPointerException.class)
    public void createUserSettings_nullInput_throwsNullPointerException() {
        userSettingsService.createUserSettings(null);
    }

    @Test(expected = UserSettingsConflictException.class)
    public void createUserSettings_userSettingsAlreadyExists_throwsIllegalArgumentException() throws UserSettingsConflictException {
        when(userSettingsRepository.existsById(validUserSettings.getId())).thenReturn(true);

        userSettingsService.createUserSettings(validUserSettings.toDTO());
        
        verify(userSettingsRepository, Mockito.times(1)).existsById(validUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).save(validUserSettings);
    }

    @Test(expected = RuntimeException.class)
    public void createUserSettings_saveReturnsEmptyUserSettings_throwsRuntimeException() {
        when(userSettingsRepository.save(validUserSettings)).thenReturn(null);
        
        userSettingsService.createUserSettings(validUserSettings.toDTO());
    }

    @Test(expected = RuntimeException.class)
    public void createUserSettings_repositoryFails_throwsRuntimeException() {
        when(userSettingsRepository.save(validUserSettings)).thenThrow(new RuntimeException());
        
        userSettingsService.createUserSettings(validUserSettings.toDTO());
    }

    @Test
    public void getUserSettingsById_validInput_returnsUserSettings() {
        when(userSettingsRepository.findById(validUserSettings.getId())).thenReturn(Optional.of(validUserSettings));

        Optional<UserSettingsDTO> retrievedUserSettings = userSettingsService.getUserSettingsById(validUserSettings.getId());

        assertNotNull(retrievedUserSettings);
        assertTrue(retrievedUserSettings.isPresent());
        assertEquals(validUserSettings.toDTO(), retrievedUserSettings.get());
        verify(userSettingsRepository, Mockito.times(1)).findById(validUserSettings.getId());
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
        when(userSettingsRepository.save(validUserSettings)).thenReturn(validUserSettings);
        when(userSettingsRepository.save(updatedUserSettings)).thenReturn(updatedUserSettings);
        when(userSettingsRepository.existsById(updatedUserSettings.getId())).thenReturn(true);
        
        UserSettings newUserSettings = userSettingsService.updateUserSettings(updatedUserSettings.toDTO()).toDomainObject();

        assertEquals(updatedUserSettings, newUserSettings);
        assertNotEquals(validUserSettings, updatedUserSettings);
        verify(userSettingsRepository, Mockito.times(1)).save(UserSettingsServiceTest.this.updatedUserSettings);
    }

    @Test(expected = UserSettingsInvalidException.class)
    public void updateUserSettings_invalidInput_throwsUserSettingsInvalidException() throws UserSettingsInvalidException {
        when(userSettingsRepository.save(validUserSettings)).thenReturn(validUserSettings);
        when(userSettingsRepository.existsById(validUserSettings.getId())).thenReturn(true);

        UserSettingsDTO invalidUserSettingsDTO = invalidUserSettingsDTO();
        invalidUserSettingsDTO.setId(validUserSettingsDTO().getId());
        
        userSettingsService.updateUserSettings(invalidUserSettingsDTO);
    }

    @Test(expected = NullPointerException.class)
    public void updateUserSettings_nullInput_throwsNullPointerException() {
        userSettingsService.updateUserSettings(null);
    }

    @Test(expected = UserSettingsNotFoundException.class)
    public void updateUserSettings_userSettingsDoesNotExist_throwsUserSettingsNotFoundException() {
        userSettingsService.updateUserSettings(updatedUserSettings.toDTO());
    }

    @Test
    public void deleteUserSettings_validInput_settingsDeleted() {
        when(userSettingsRepository.save(validUserSettings)).thenReturn(validUserSettings);
        when(userSettingsRepository.existsById(validUserSettings.getId())).thenReturn(true);

        userSettingsService.deleteUserSettings(validUserSettings.getId());
        
        verify(userSettingsRepository, Mockito.times(1)).deleteById(validUserSettings.getId());
        verify(userSettingsRepository, Mockito.times(1)).existsById(validUserSettings.getId());
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
