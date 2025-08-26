package com.kiwi.settings;

import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.settings.controllers.SettingsRepository;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kiwi.settings.SettingsTestFactory.settingsDTO;
import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
public class SettingsRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    private final UsersPersistence user = UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword());
    private final SettingsPersistence settingsPersistence = SettingsDataMapper.toPersistence(user, settingsDTO());

    @Test
    public void getSettings_validId_returnsSettings() {
        usersRepository.saveAndFlush(user);
        settingsRepository.saveAndFlush(settingsPersistence);
        assertNotNull(settingsRepository.findByUserEmail(user.getEmail()));
    }

    @Test
    public void getSettings_invalidId_returnsEmptyOptional() {
        assertEquals(Optional.empty(), settingsRepository.findByUserEmail(invalidUserDTO().getEmail()));
    }

    @Test
    public void getSettings_settingsDoesNotExist_returnsEmptyOptional() {
        assertEquals(Optional.empty(), settingsRepository.findByUserEmail(validUserDTO().getEmail()));
    }
}