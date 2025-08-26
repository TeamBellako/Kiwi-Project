package com.kiwi.settings;

import com.kiwi.features.settings.controllers.SettingsRepository;
import com.kiwi.features.settings.controllers.SettingsService;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.settings.SettingsTestFactory.*;

import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SettingsServiceTest {

    private final SettingsRepository settingsRepository = Mockito.mock(SettingsRepository.class);
    private final UsersService usersService = Mockito.mock(UsersService.class);
    private final SettingsService settingsService = new SettingsService(settingsRepository, usersService);

    private final UsersPersistence user = UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword());
    private final SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(user, settingsDTO());

    @Test
    public void getSettings_validInput_returnsSettings() {
        when(settingsRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsPersistence retrievedSettings = settingsService.getSettings(user.getEmail());

        assertNotNull(retrievedSettings);
        assertEquals(settingsPersistence, retrievedSettings);
        verify(settingsRepository, Mockito.times(1)).findByUserEmail(user.getEmail());
    }

    @Test(expected = SettingsNotFoundException.class)
    public void getSettings_settingsDoesNotExist_created() {
        SettingsPersistence retrievedSettings = settingsService.getSettings("a" + user.getEmail());
        assertNotNull(retrievedSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSettings_invalidInput_throwsIllegalArgumentException() {
        settingsService.getSettings(invalidUserDTO().getEmail());
    }

    @Test
    public void updateSettings_validInput_settingsUpdated() {
        when(settingsRepository.saveAndFlush(settingsPersistence)).thenReturn(settingsPersistence);
        when(settingsRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsDTO newSettingsDTO = settingsService.updateSettings(user.getEmail(), settingsDTO());

        assertEquals(newSettingsDTO, settingsDTO());
        verify(settingsRepository, Mockito.times(1)).saveAndFlush(SettingsServiceTest.this.settingsPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateSettings_nullInput_throwsNullPointerException() {
        settingsService.updateSettings(user.getEmail(), null);
    }

    @Test(expected = SettingsNotFoundException.class)
    public void updateSettings_settingsDoesNotExist_throwsSettingsNotFoundException() {
        settingsService.updateSettings(user.getEmail(), settingsDTO());
    }
}
