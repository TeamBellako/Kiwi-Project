package com.kiwi.settings;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.types.Email;
import com.kiwi.features.settings.controllers.SettingsRepository;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.kiwi.settings.SettingsTestFactory.settingsDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc 
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class SettingsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private SettingsRepository settingsRepository;

    private final String baseAPIUrl = "/api/user/settings";

    @Before
    public void setUp() {
        UsersDomain userDomain = UsersDataMapper.toDomain(validUserDTO());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistence(userDomain, validUserDTO().getPassword());
        usersRepository.saveAndFlush(usersPersistence);
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getSettings_validInput_returnsCurrentSettings() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        settingsRepository.save(SettingsDataMapper.toPersistence(savedUser, settingsDTO()));

        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsDTO())))
                .andExpect(status().isOk());
    }
    
    @Test
    public void getSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_validInput_returnsUpdatedSettings() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        settingsRepository.save(SettingsDataMapper.toPersistence(savedUser, settingsDTO()));

        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsDTO())))
                .andExpect(status().isOk());
        
        assertTrue(settingsRepository.findByUserEmail(validUserDTO().getEmail()).isPresent());
        assertEquals(settingsDTO(), SettingsDataMapper.toDTO(settingsRepository.findByUserEmail(validUserDTO().getEmail()).get()));
    }

    @Test
    public void updateSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(put(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settingsDTO())))
                .andExpect(status().isUnauthorized());
    }
}

