package com.kiwi.settings;

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

import static com.kiwi.settings.SettingsTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/DBTestSetUp.sql")
@ActiveProfiles("test")
public class SettingsRepositoryTest {
    
    @Autowired
    private SettingsRepository settingsRepository;

    @Test
    public void getSettings_validId_returnsSettings() {
        settingsRepository.saveAndFlush(validSettingsDTO().toDomainObject());
        
        assertNotNull(settingsRepository.findByEmail(validSettingsDTO().getEmail()));
    }

    @Test
    public void getSettings_invalidId_returnsEmptyOptional() {
        assertEquals(Optional.empty(), settingsRepository.findByEmail(invalidSettingsDTO().getEmail()));
    }

    @Test
    public void getSettings_settingsDoesNotExist_returnsEmptyOptional() {
        assertEquals(Optional.empty(), settingsRepository.findByEmail(validSettingsDTO().getEmail() + 1));
    }

    @Test
    public void updateSettings_validInput_updatesSettings() {
        settingsRepository.saveAndFlush(updatedSettingsDTO().toDomainObject());
        
        assertEquals(settingsRepository.findByEmail(updatedSettingsDTO().getEmail()).get(), updatedSettingsDTO().toDomainObject());
    }

    @Test
    public void updateSettings_settingsDoesNotExist_createsUserSetting() {
        Settings saved = settingsRepository.saveAndFlush(validSettingsDTO().toDomainObject());
        Optional<Settings> result = settingsRepository.findByEmail(saved.getEmail().value());
        
        assertEquals(validSettingsDTO().toDomainObject(), result.get());
    }
}