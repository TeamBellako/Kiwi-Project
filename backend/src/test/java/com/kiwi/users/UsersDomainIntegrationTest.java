package com.kiwi.users;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.*;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
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
public class UsersDomainIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UsersService usersService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;
    
    private final String signupAPIUrl = "/api/public/signup";
    private final String loginAPIUrl = "/api/public/login";

    @Test
    public void validSignup() throws Exception {
        UsersDTO userDTO = validUserDTO();
        LoginDTO loginDTO = new LoginDTO(userDTO.getEmail(), userDTO.getPassword());

        mockMvc.perform(getPostRequestBuilder(signupAPIUrl, loginDTO))
                .andExpect(status().isCreated());

        Optional<UsersPersistence> savedUserOpt = usersRepository.findByEmail(userDTO.getEmail());
        assertTrue(savedUserOpt.isPresent());
        UsersPersistence savedUser = savedUserOpt.get();

        assertTrue(passwordEncoder.matches(userDTO.getPassword(), savedUser.getPassword()));
        assertEquals(userDTO.getEmail(), savedUser.getEmail().value());
    }

    @Test
    public void invalidSignup() throws Exception {
        mockMvc.perform(getPostRequestBuilder(signupAPIUrl, getinValidLoginDTO()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void duplicatedSignup() throws Exception {
        UsersDomain user = UsersDataMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersDataMapper.toPersistence(user, validUserDTO().getPassword()));
        
        mockMvc.perform(getPostRequestBuilder(signupAPIUrl, getValidLoginDTO()))
                .andExpect(status().isConflict());
    }

    @Test
    public void validLogin() throws Exception {
        UsersDomain user = UsersDataMapper.toDomain(validUserDTO());
        String hashedPassword = passwordEncoder.encode(validUserDTO().getPassword());
        usersRepository.saveAndFlush(UsersDataMapper.toPersistence(user, hashedPassword));
        
        MvcResult result = mockMvc.perform(getPostRequestBuilder(loginAPIUrl, getValidLoginDTO()))
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
        mockMvc.perform(getPostRequestBuilder(loginAPIUrl, getinValidLoginDTO()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nonExistingLogin() throws Exception {
        mockMvc.perform(getPostRequestBuilder(loginAPIUrl, getValidLoginDTO()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void incorrectPasswordLogin() throws Exception {
        UsersDomain user = UsersDataMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersDataMapper.toPersistence(user, validUserDTO().getPassword()));
        
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), "Marceline*Simon4Ever");

        mockMvc.perform(getPostRequestBuilder(loginAPIUrl, loginDTO))
                .andExpect(status().isUnauthorized());
    }
    
    private LoginDTO getValidLoginDTO() { return new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword()); }
    private LoginDTO getinValidLoginDTO() { return new LoginDTO(invalidUserDTO().getEmail(), invalidUserDTO().getPassword()); }
}
