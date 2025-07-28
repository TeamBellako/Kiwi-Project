package com.kiwi.settings;

import com.kiwi.features.settings.*;
import com.kiwi.features.users.UsersInvalidException;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.settings.SettingsTestFactory.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsServiceTest {

    private final SettingsRepository settingsRepository = Mockito.mock(SettingsRepository.class);
    private final SettingsService settingsService = new SettingsService(settingsRepository);
    
    private final Settings validSettings = validSettingsDTO().toDomainObject();
    private final Settings updatedSettings = updatedSettingsDTO().toDomainObject();

    @Test
    public void getSettings_validInput_returnsSettings() {
        when(settingsRepository.findByEmail(validSettings.getEmail().value())).thenReturn(Optional.of(validSettings));

        Optional<SettingsDTO> retrievedSettings = settingsService.getSettingsByEmail(validSettings.getEmail().value());

        assertNotNull(retrievedSettings);
        assertTrue(retrievedSettings.isPresent());
        assertEquals(validSettings.toDTO(), retrievedSettings.get());
        verify(settingsRepository, Mockito.times(1)).findByEmail(validSettings.getEmail().value());
    }

    @Test(expected = SettingsInvalidException.class)
    public void getSettings_invalidInput_throwsSettingsInvalidException() {
        settingsService.getSettingsByEmail(invalidSettingsDTO().getEmail());
    }

    @Test(expected = SettingsNotFoundException.class)
    public void getSettings_settingsDoesNotExist_throwsSettingsNotFoundException() {
        settingsService.getSettingsByEmail(updatedSettingsDTO().getEmail());
    }

    @Test
    public void updateSettings_validInput_settingsUpdated() {
        when(settingsRepository.saveAndFlush(validSettings)).thenReturn(validSettings);
        when(settingsRepository.saveAndFlush(updatedSettings)).thenReturn(updatedSettings);
        when(settingsRepository.findByEmail(updatedSettings.getEmail().value())).thenReturn(Optional.of(validSettings));
        
        Settings newSettings = settingsService.updateSettings(updatedSettings.toDTO()).toDomainObject();

        assertEquals(updatedSettings, newSettings);
        verify(settingsRepository, Mockito.times(1)).saveAndFlush(SettingsServiceTest.this.updatedSettings);
    }

    @Test(expected = SettingsInvalidException.class)
    public void updateSettings_invalidInput_throwsUsersInvalidException() throws SettingsInvalidException {
        when(settingsRepository.existsByEmail(invalidSettingsDTO().getEmail())).thenReturn(true);
        
        settingsService.updateSettings(invalidSettingsDTO());
    }

    @Test(expected = NullPointerException.class)
    public void updateSettings_nullInput_throwsNullPointerException() {
        settingsService.updateSettings(null);
    }

    @Test(expected = SettingsNotFoundException.class)
    public void updateSettings_settingsDoesNotExist_throwsSettingsNotFoundException() {
        settingsService.updateSettings(updatedSettings.toDTO());
    }
}
