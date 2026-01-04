package com.kiwi.personality;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.personality.data.PersonalityDataMapper;
import com.kiwi.features.personality.data.PersonalityPersistence;
import com.kiwi.features.personality.controllers.PersonalityRepository;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.security.JwtUtils;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
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

import static com.kiwi.personality.PersonalityTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc 
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class PersonalityIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PersonalityRepository personalityRepository;

    private final String baseAPIUrl = "/api/user/personality";

    @Before
    public void setUp() {
        UsersDomain userDomain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistence(userDomain, validLoginDTO().getPassword());
        usersRepository.saveAndFlush(usersPersistence);
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getPersonality_valid() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        mockMvc.perform(get(baseAPIUrl)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getPersonality_invalid() throws Exception {
        mockMvc.perform(get(baseAPIUrl)).andExpect(status().isNotFound());
    }
    
    @Test
    public void getPersonality_unauthorized() throws Exception {
        mockMvc.perform(get(baseAPIUrl)).andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateRealName() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        mockMvc.perform(post(baseAPIUrl + "/realName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userNameRealDTO())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateRealName_unauthorized() throws Exception {
        mockMvc.perform(post(baseAPIUrl + "/realName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userNameRealDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateKnightName() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        mockMvc.perform(post(baseAPIUrl + "/knightName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userNameKnightDTO())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateKnightName_unauthorized() throws Exception {
        mockMvc.perform(post(baseAPIUrl + "/knightName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userNameKnightDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateBuild() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        mockMvc.perform(post(baseAPIUrl + "/build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBuild_unauthorized() throws Exception {
        mockMvc.perform(post(baseAPIUrl + "/build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateApps() throws Exception {
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        mockMvc.perform(post(baseAPIUrl + "/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appsDTO())))
                .andExpect(status().isOk());
    }

    @Test
    public void updateApps_unauthorized() throws Exception {
        mockMvc.perform(post(baseAPIUrl + "/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildDTO())))
                .andExpect(status().isUnauthorized());
    }
}
