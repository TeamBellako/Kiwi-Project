package com.kiwi.nodes;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.security.JwtUtils;

import com.kiwi.features.nodes.controllers.NodesRepository;
import com.kiwi.features.nodes.controllers.UserNodeStatusRepository;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusKey;

import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;

import org.junit.Before;
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

import java.util.List;

import static com.kiwi.nodes.NodesTestFactory.persistenceNode;
import static com.kiwi.nodes.NodesTestFactory.openStatus;
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
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class})
public class NodesIntegrationTests {

    private final String API_URL = "/api/nodes";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsersRepository usersRepository;
    @Autowired private NodesRepository nodesRepository;
    @Autowired private UserNodeStatusRepository statusRepository;

    private UsersPersistence validUserPersistence;

    @Before
    public void setUp() {
        UsersDomain domain = UsersDataMapper.toDomain(validUserDTO());
        validUserPersistence = usersRepository.saveAndFlush(
                UsersDataMapper.toPersistence(domain, validUserDTO().getPassword())
        );

        // Insert sample nodes
        List.of(
                persistenceNode(1, 1, 100,0,0),
                persistenceNode(2, 2, 150,0,0),
                persistenceNode(3, 3, 200, 0,0)
        ).forEach(nodesRepository::saveAndFlush);

        // Initial open node for user
        statusRepository.saveAndFlush(openStatus(validUserPersistence.getId(), 1));
    }

    /* ------------------- GET NODES --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAllNodes() throws Exception {
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getNodeById() throws Exception {
        mockMvc.perform(get(API_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getNodeNotFound() throws Exception {
        mockMvc.perform(get(API_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    /* ------------------- UNLOCK NODE --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void unlockNodeSuccess() throws Exception {
        mockMvc.perform(post(API_URL + "/2/unlock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.OPEN.toString()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void unlockNodeFailsIfBlocked() throws Exception {
        statusRepository.saveAndFlush(new UserNodeStatusPersistence(
                new UserNodeStatusKey(validUserPersistence.getId(), 1),
                NodeStatus.LOCKED
        ));

        mockMvc.perform(post(API_URL + "/2/unlock"))
                .andExpect(status().isForbidden());
    }

    /* ------------------- COMPLETE NODE --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeNodeSuccess() throws Exception {
        mockMvc.perform(post(API_URL + "/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.COMPLETED.toString()));
    }

    /* ------------------- LOCK NEXT NODE --------------------- */

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void lockNextNodeSuccess() throws Exception {
        mockMvc.perform(post(API_URL + "/2/lock-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.LOCKED.toString()));
    }
}
