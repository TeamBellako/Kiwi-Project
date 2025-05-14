package com.kiwi.users;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.exception.GlobalExceptionHandler;
import com.kiwi.security.JwtUtils;
import com.kiwi.security.WebSecurityConfig;
import com.kiwi.usersettings.UserSettingsDTO;
import com.kiwi.usersettings.UserSettingsRepository;
import com.kiwi.usersettings.UserSettingsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static com.kiwi.users.UsersTestHTTPUtils.getValidLogInPostRequestBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/UsersTestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class UsersIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtUtils jwtUtils;
    
    private final String signupAPIUrl = "/api/public/signup";
    private final String loginAPIUrl = "/api/public/login";
    
    @Test
    public void validSignup() throws Exception {
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());
        
        mockMvc.perform(getValidLogInPostRequestBuilder(signupAPIUrl, loginDTO))
                .andExpect(status().isOk());
        
        assertEquals(validUserDTO(), usersRepository.findByEmail(validUserDTO().getEmail()).get().toDTO());
    }

    @Test
    public void invalidSignup() throws Exception {
        LoginDTO loginDTO = new LoginDTO(invalidUserDTO().getEmail(), invalidUserDTO().getPassword());
        
        mockMvc.perform(getValidLogInPostRequestBuilder(signupAPIUrl, loginDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void duplicatedSignup() throws Exception {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());

        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());

        mockMvc.perform(getValidLogInPostRequestBuilder(signupAPIUrl, loginDTO))
                .andExpect(status().isConflict());
    }

    @Test
    public void validLogin() throws Exception {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());
        
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());
        MvcResult result = mockMvc.perform(getValidLogInPostRequestBuilder(loginAPIUrl, loginDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists())
                .andReturn();
        
        String responseBody = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        String jwtToken = objectMapper.readTree(responseBody).get("jwt").asText();
        
        assertTrue(jwtUtils.validateJwtToken(jwtToken));
    }

    @Test
    public void invalidLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO(invalidUserDTO().getEmail(), invalidUserDTO().getPassword());

        mockMvc.perform(getValidLogInPostRequestBuilder(loginAPIUrl, loginDTO))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nonExistingLogin() throws Exception {
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());

        mockMvc.perform(getValidLogInPostRequestBuilder(loginAPIUrl, loginDTO))
                .andExpect(status().isNotFound());
    }

    @Test
    public void incorrectPasswordLogin() throws Exception {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());
        
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), "Marceline*Simon4Ever");

        mockMvc.perform(getValidLogInPostRequestBuilder(loginAPIUrl, loginDTO))
                .andExpect(status().isUnauthorized());
    }
}
