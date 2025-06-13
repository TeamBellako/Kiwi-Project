package com.kiwi.settings;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.GlobalExceptionHandler;
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

import static com.kiwi.settings.SettingsTestFactory.updatedSettingsDTO;
import static com.kiwi.settings.SettingsTestHTTPUtils.getPUTRequestContent;
import static com.kiwi.settings.SettingsTestHTTPUtils.getSettingsResultMatcher;
import static com.kiwi.settings.SettingsTestFactory.validSettingsDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/SettingsTestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc 
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class SettingsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private SettingsService settingsService;

    @Autowired
    private JwtUtils jwtUtils;
    

    private final String baseAPIUrl = "/api/user/settings";

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getSettings_validInput_returnsCurrentSettings() throws Exception {
        settingsRepository.save(validSettingsDTO().toDomainObject());
        
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isOk())
            .andExpect(getSettingsResultMatcher(validSettingsDTO()));
    }
    
    @Test
    public void getSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(baseAPIUrl))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateSettings_validInput_returnsUpdatedSettings() throws Exception {
        settingsRepository.save(validSettingsDTO().toDomainObject());
        
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, updatedSettingsDTO()))
            .andExpect(status().isOk())
            .andExpect(getSettingsResultMatcher(updatedSettingsDTO()));
        
        assertEquals(updatedSettingsDTO().toDomainObject(), settingsRepository.findByEmail(validSettingsDTO().getEmail()).get());
    }

    @Test
    public void updateSettings_unauthorizedUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(getPUTRequestContent(baseAPIUrl, validSettingsDTO()))
                .andExpect(status().isUnauthorized());
    }
}

