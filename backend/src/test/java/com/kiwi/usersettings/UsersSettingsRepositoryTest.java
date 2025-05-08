package com.kiwi.usersettings;

import com.kiwi.security.CustomUserDetailsService;
import com.kiwi.security.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
@Sql(scripts = "/UserSettingsTestSetUp.sql")
@ActiveProfiles("test")
public class UsersSettingsRepositoryTest {
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @MockitoBean
    private JwtUtils jwtUtils;
    
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    public void getUserSettings_validId_returnsUserSettings() {
        assertNotNull(userSettingsRepository.findById(validUserSettingsDTO().getEmail()));
    }

    @Test
    public void getUserSettings_invalidId_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(invalidUserSettingsDTO().getEmail()));
    }

    @Test
    public void getUserSettings_userSettingsDoesNotExist_returnsEmptyOptional() {
        assertEquals(Optional.empty(), userSettingsRepository.findById(validUserSettingsDTO().getEmail() + 1));
    }

    @Test
    public void updateUserSettings_validInput_updatesUserSettings() {
        userSettingsRepository.saveAndFlush(updatedUserSettingsDTO().toDomainObject());
        
        assertEquals(userSettingsRepository.findById(updatedUserSettingsDTO().getEmail()).get(), updatedUserSettingsDTO().toDomainObject());
    }

    @Test
    public void updateUserSettings_userSettingsDoesNotExist_createsUserSetting() {
        UserSettings saved = userSettingsRepository.saveAndFlush(validUserSettingsDTO().toDomainObject());
        Optional<UserSettings> result = userSettingsRepository.findById(saved.getEmail());
        
        assertEquals(validUserSettingsDTO().toDomainObject(), result.get());
    }
}