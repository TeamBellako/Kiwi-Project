package com.kiwi.usersettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kiwi.usersettings.UserSettingsTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/UserSettingsIntegrationTest.sql")
@ActiveProfiles("test")
public class UserSettingsIntegrationTest {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Test
    public void createUserSettings_validInput_createsUserSettings() {
        assertEquals(noIdUserSettings(), userSettingsRepository.saveAndFlush(noIdUserSettings()));
    }

    @Test
    public void getUserSettingsById_validId_returnsUserSettings() {
        assertNotNull(userSettingsRepository.findById(validUserSettingsDTO().getId()));
    }

    @Test
    public void getUserSettingsById_invalidId_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(invalidUserSettingsDTO().getId()));
    }

    @Test
    public void getUserSettingsById_userSettingsDoesNotExist_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(validUserSettingsDTO().getId() + 1));
    }

    @Test
    public void updateUserSettings_validInput_updatesUserSettings() {
        userSettingsRepository.saveAndFlush(updatedUserSettingsDTO().toDomainObject());
        
        assertEquals(userSettingsRepository.findById(validUserSettingsDTO().getId()).get(), updatedUserSettingsDTO().toDomainObject());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_createsUserSetting() {
        UserSettings saved = userSettingsRepository.saveAndFlush(noIdUserSettings());
        Optional<UserSettings> result = userSettingsRepository.findById(saved.getId());
        
        assertEquals(noIdUserSettings(), result.get());
    }
    
    @Test
    public void deleteUserSettings_validId_deletesUserSettings() {
        userSettingsRepository.deleteById(validUserSettingsDTO().getId());
        
        assertEquals(Optional.empty(), userSettingsRepository.findById(validUserSettingsDTO().getId()));
    }

    @Test
    public void deleteUserSettings_invalidId_doesNothing() {
        userSettingsRepository.deleteById(invalidUserSettingsDTO().getId());
        
        assertNotNull(userSettingsRepository.findById(validUserSettingsDTO().getId()).get());
    }

    @Test
    public void deleteUserSettings_userDoesNotExists_doesNothing() {
        userSettingsRepository.deleteById(validUserSettingsDTO().getId() + 1);

        assertNotNull(userSettingsRepository.findById(validUserSettingsDTO().getId()).get());
    }
}