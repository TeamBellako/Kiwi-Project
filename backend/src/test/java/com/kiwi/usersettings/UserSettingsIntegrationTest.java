package com.kiwi.usersettings;

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
    
    private final Integer targetId = 2;
    
    @Test
    public void createUserSettings_validInput_createsUserSettings() {
        UserSettings userSettings = new UserSettings(
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
        
        assertEquals(userSettings, userSettingsRepository.save(userSettings));
    }

    @Test
    public void getUserSettingsById_validId_returnsUserSettings() {
        assertNotNull(userSettingsRepository.findById(targetId));
    }

    @Test
    public void updateUserSettings_validInput_updatesUserSettings() {
        Optional<UserSettings> userSettings = userSettingsRepository.findById(targetId);
        if (userSettings.isEmpty()) throw new UserSettingsNotFoundException(targetId);
        
        UserSettings updatedUserSettings = new UserSettings(
                targetId,
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
        
        assertNotEquals(userSettings.get(), updatedUserSettings);
        userSettingsRepository.save(updatedUserSettings);
        assertEquals(userSettings.get(), updatedUserSettings);
    }

    @Test
    public void deleteUserSettings_validId_deletesUserSettings() {
        assertNotNull(userSettingsRepository.findById(targetId));
        userSettingsRepository.deleteById(targetId);
        assertEquals(Optional.empty(), userSettingsRepository.findById(targetId));
    }
}