package com.kiwi.nodes;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.nodes.controllers.NodesRepository;
import com.kiwi.features.nodes.controllers.UserNodeStatusRepository;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kiwi.nodes.NodesTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class })
public class NodesIntegrationTests {

    private final String API_URL = "/api/nodes";

    @Autowired private MockMvc mockMvc;
    @Autowired private UsersRepository usersRepository;
    @Autowired private UserNodeStatusRepository statusRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @Before
    public void setUp() {
        UsersDomain userDomain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistence(userDomain, validLoginDTO().getPassword());
        usersRepository.saveAndFlush(usersPersistence);

        statusRepository.saveAndFlush(lockedStatus(usersPersistence.getId(), 1L));
        statusRepository.saveAndFlush(openStatus(usersPersistence.getId(), 2L));
        statusRepository.saveAndFlush(completeStatus(usersPersistence.getId(), 3L));
    }

    /* ------------------- GET NODES --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAllNodes() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    /* ------------------- UNLOCK NODE --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void unlockNodeSuccess() throws Exception {
        mockMvc.perform(post(API_URL + "/1/unlock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.OPEN.toString()));
    }

    /* ------------------- COMPLETE NODE --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeNodeSuccess() throws Exception {
        mockMvc.perform(post(API_URL + "/2/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.COMPLETED.toString()));
    }

}
