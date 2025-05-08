package com.kiwi.usersettings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.kiwi.usersettings.UserSettingsHTTPUtils.getPUTRequestContent;
import static com.kiwi.usersettings.UserSettingsHTTPUtils.getUserSettingsResultMatcher;
import static com.kiwi.usersettings.UserSettingsTestFactory.updatedUserSettingsDTO;
import static com.kiwi.usersettings.UserSettingsTestFactory.validUserSettingsDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/UserSettingsTestSetUp.sql")
@ActiveProfiles("test")
public class UsersSettingsEndToEndTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserSettingsService userSettingsService;

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
    @WithMockUser(username = "finn@thehuman.com")
    public void updateUserSettings_validInput_returnsUpdatedSettings() throws Exception {
        userSettingsRepository.save(validUserSettingsDTO().toDomainObject());

        mockMvc.perform(getPUTRequestContent(baseAPIUrl, updatedUserSettingsDTO()))
            .andExpect(status().isOk())
            .andExpect(getUserSettingsResultMatcher(updatedUserSettingsDTO()));
    }
}

