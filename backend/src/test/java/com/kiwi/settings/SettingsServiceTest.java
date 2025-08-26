package com.kiwi.settings;

import com.kiwi.common.types.Email;
import com.kiwi.features.settings.controllers.SettingsRepository;
import com.kiwi.features.settings.controllers.SettingsService;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.features.users.controllers.UsersService;
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

    @Test
    public void getSettings_validInput_returnsSettings() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(savedUser, settingsDTO());
        when(settingsRepository.findByUserEmail(savedUser.getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsPersistence retrievedSettings = settingsService.getSettings(savedUser.getEmail());

        assertNotNull(retrievedSettings);
        assertEquals(settingsPersistence, retrievedSettings);
        verify(settingsRepository, Mockito.times(1)).findByUserEmail(savedUser.getEmail());
    }

    @Test(expected = SettingsNotFoundException.class)
    public void getSettings_settingsDoesNotExist_created() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        SettingsPersistence retrievedSettings = settingsService.getSettings("a" + savedUser.getEmail());
        assertNotNull(retrievedSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSettings_invalidInput_throwsIllegalArgumentException() {
        settingsService.getSettings(invalidUserDTO().getEmail());
    }

    @Test
    public void updateSettings_validInput_settingsUpdated() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(savedUser, settingsDTO());
        when(settingsRepository.saveAndFlush(settingsPersistence)).thenReturn(settingsPersistence);
        when(settingsRepository.findByUserEmail(savedUser.getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsDTO newSettingsDTO = settingsService.updateSettings(savedUser.getEmail(), settingsDTO());

        assertEquals(newSettingsDTO, settingsDTO());
        verify(settingsRepository, Mockito.times(1)).saveAndFlush(settingsPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateSettings_nullInput_throwsNullPointerException() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        settingsService.updateSettings(savedUser.getEmail(), null);
    }

    @Test(expected = SettingsNotFoundException.class)
    public void updateSettings_settingsDoesNotExist_throwsSettingsNotFoundException() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        settingsService.updateSettings(savedUser.getEmail(), settingsDTO());
    }
}
