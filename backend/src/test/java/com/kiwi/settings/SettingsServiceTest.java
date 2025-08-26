package com.kiwi.settings;

import com.kiwi.common.types.Email;
import com.kiwi.features.settings.controllers.SettingsRepository;
import com.kiwi.features.settings.controllers.SettingsService;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import org.junit.Before;
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

    @Before
    public void setUp() {
        usersService.createUser(validUserDTO());
    }

    @Test
    public void getSettings_validInput_returnsSettings() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(savedUser, settingsDTO());
        when(settingsRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsPersistence retrievedSettings = settingsService.getSettings(validUserDTO().getEmail());

        assertNotNull(retrievedSettings);
        assertEquals(settingsPersistence, retrievedSettings);
        verify(settingsRepository, Mockito.times(1)).findByUserEmail(validUserDTO().getEmail());
    }

    @Test(expected = SettingsNotFoundException.class)
    public void getSettings_settingsDoesNotExist_created() {
        SettingsPersistence retrievedSettings = settingsService.getSettings("a" + validUserDTO().getEmail());
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
        when(settingsRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(settingsPersistence));

        SettingsDTO newSettingsDTO = settingsService.updateSettings(validUserDTO().getEmail(), settingsDTO());

        assertEquals(newSettingsDTO, settingsDTO());
        verify(settingsRepository, Mockito.times(1)).saveAndFlush(settingsPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateSettings_nullInput_throwsNullPointerException() {
        when(usersService.getUserByEmail(new Email(validUserDTO().getEmail())))
                .thenReturn(Optional.of(UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword())));

        settingsService.updateSettings(validUserDTO().getEmail(), null);
    }

    @Test(expected = UsersNotFoundException.class)
    public void updateSettings_userDoesNotExist_throwsUsersNotFoundException() {
        settingsService.updateSettings(validUserDTO().getEmail(), settingsDTO());
    }
}
