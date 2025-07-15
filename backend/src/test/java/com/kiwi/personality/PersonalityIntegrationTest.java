package com.kiwi.personality;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.personality.Personality;
import com.kiwi.features.personality.PersonalityRepository;
import com.kiwi.features.personality.PersonalityService;
import com.kiwi.features.users.Users;
import com.kiwi.features.users.UsersMapper;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersRepository;
import com.kiwi.security.JwtUtils;
import com.kiwi.utils.GlobalExceptionHandler;
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
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/UsersTestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc 
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class })
public class PersonalityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonalityRepository personalityRepository;
    @Autowired
    private UsersRepository usersRepository;

    private final String baseAPIUrl = "/api/user/personality";

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getPersonality_valid() throws Exception {
        Users user = UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();

        Personality personality = validPersonality();
        personality.setUser(savedUser);

        personalityRepository.saveAndFlush(personality);
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
        Users user = UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();

        Personality personality = validPersonality();
        personality.setUser(savedUser);

        personalityRepository.saveAndFlush(personality);
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
        Users user = UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();

        Personality personality = validPersonality();
        personality.setUser(savedUser);

        personalityRepository.saveAndFlush(personality);
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
        Users user = UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();

        Personality personality = validPersonality();
        personality.setUser(savedUser);

        personalityRepository.saveAndFlush(personality);
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
}
