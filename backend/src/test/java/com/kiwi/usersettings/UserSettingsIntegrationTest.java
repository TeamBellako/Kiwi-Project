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

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createUserSettings_invalidInput_throwsConstrainViolationException() {
        userSettingsRepository.saveAndFlush(invalidNoIdUserSettings());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createUserSettings_userSettingsAlreadyExists_throwsDataIntegrityViolationException() {
        userSettingsRepository.saveAndFlush(duplicateUserSettings());
    }

    @Test
    public void getUserSettingsById_validId_returnsUserSettings() {
        assertNotNull(userSettingsRepository.findById(validUserSettings().getId()));
    }

    @Test
    public void getUserSettingsById_invalidId_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(invalidUserSettings().getId()));
    }

    @Test
    public void getUserSettingsById_userSettingsDoesNotExist_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(validUserSettings().getId() + 1));
    }

    @Test
    public void updateUserSettings_validInput_updatesUserSettings() {
        userSettingsRepository.saveAndFlush(updatedUserSettings());
        
        assertEquals(userSettingsRepository.findById(validUserSettings().getId()).get(), updatedUserSettings());
    }

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void updateUserSettings_invalidInput_throwsConstraintViolationException() {
        userSettingsRepository.saveAndFlush(invalidNoIdUserSettings());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_createsUserSetting() {
        UserSettings saved = userSettingsRepository.saveAndFlush(noIdUserSettings());
        Optional<UserSettings> result = userSettingsRepository.findById(saved.getId());
        
        assertEquals(noIdUserSettings(), result.get());
    }


    @Test
    public void deleteUserSettings_validId_deletesUserSettings() {
        userSettingsRepository.deleteById(validUserSettings().getId());
        
        assertEquals(Optional.empty(), userSettingsRepository.findById(validUserSettings().getId()));
    }

    @Test
    public void deleteUserSettings_invalidId_doesNothing() {
        userSettingsRepository.deleteById(invalidUserSettings().getId());
        
        assertNotNull(userSettingsRepository.findById(validUserSettings().getId()).get());
    }

    @Test
    public void deleteUserSettings_userDoesNotExists_doesNothing() {
        userSettingsRepository.deleteById(validUserSettings().getId() + 1);

        assertNotNull(userSettingsRepository.findById(validUserSettings().getId()).get());
    }
}