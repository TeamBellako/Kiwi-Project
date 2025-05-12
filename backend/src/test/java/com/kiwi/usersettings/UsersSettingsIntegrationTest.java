package com.kiwi.usersettings;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.exception.GlobalExceptionHandler;
import com.kiwi.security.JwtUtils;
import com.kiwi.security.WebSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.kiwi.usersettings.UserSettingsTestFactory.updatedUserSettingsDTO;
import static com.kiwi.usersettings.UserSettingsTestHTTPUtils.getPUTRequestContent;
import static com.kiwi.usersettings.UserSettingsTestHTTPUtils.getUserSettingsResultMatcher;
import static com.kiwi.usersettings.UserSettingsTestFactory.validUserSettingsDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/UserSettingsTestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc 
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class UsersSettingsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private JwtUtils jwtUtils;
    

    private final String baseAPIUrl = "/api/settings";

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getUserSettings_validInput_returnsCurrentSettings() throws Exception {
        userSettingsRepository.save(validUserSettingsDTO().toDomainObject());
        
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isOk())
            .andExpect(getUserSettingsResultMatcher(validUserSettingsDTO()));
    }
    
    @Test
    public void getUserSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_validInput_returnsUpdatedSettings() throws Exception {
        userSettingsRepository.save(validUserSettingsDTO().toDomainObject());
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, updatedUserSettingsDTO()))
            .andExpect(status().isOk())
            .andExpect(getUserSettingsResultMatcher(updatedUserSettingsDTO()));
        
        assertEquals(updatedUserSettingsDTO().toDomainObject(), userSettingsRepository.findByEmail(validUserSettingsDTO().getEmail()).get());
    }

    @Test
    public void updateUserSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validUserSettingsDTO()))
                .andExpect(status().isUnauthorized());
    }
}

