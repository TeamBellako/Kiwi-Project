package com.kiwi.nodes;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.nodes.controllers.NodesRepository;
import com.kiwi.features.nodes.controllers.UserNodeStatusRepository;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesPersistence;
import com.kiwi.features.nodes.data.UserNodeStatusPersistence;
import com.kiwi.features.nodes.exceptions.NodeInaccessibleException;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
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

import java.util.List;

import static com.kiwi.nodes.NodesTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Autowired private NodesRepository nodesRepository;
    @Autowired private UserNodeStatusRepository statusRepository;
    @Autowired private ObjectMapper objectMapper;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser() {
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence user = UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        return usersRepository.saveAndFlush(user);
    }

    private NodesPersistence createNode(Long nodeId) {
        return nodesRepository.saveAndFlush(persistenceNode(nodeId));
    }

    private void setNodeStatuses(UsersPersistence user, NodesPersistence... nodes) {
        for (NodesPersistence node : nodes) {
            UserNodeStatusPersistence status;
            switch (node.getId().intValue()) {
                case 1 -> status = lockedStatus(user.getId(), node.getId());
                case 2 -> status = openStatus(user.getId(), node.getId());
                case 3 -> status = completeStatus(user.getId(), node.getId());
                default -> status = openStatus(user.getId(), node.getId());
            }
            statusRepository.saveAndFlush(status);
        }
    }

    // ============================================================
    // TESTS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAllNodes() throws Exception {
        var user = createUser();
        var n1 = createNode(1L);
        var n2 = createNode(2L);
        var n3 = createNode(3L);
        var n4 = createNode(4L);

        setNodeStatuses(user, n1, n2, n3, n4);

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void unlockNodeSuccess() throws Exception {
        var user = createUser();
        var node = createNode(1L);
        statusRepository.saveAndFlush(lockedStatus(user.getId(), node.getId()));

        mockMvc.perform(post(API_URL + "/" + node.getId() + "/unlock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(NodeStatus.OPEN.toString()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeNodeSuccess() throws Exception {
        var user = createUser();
        var node = createNode(2L);

        statusRepository.saveAndFlush(openStatus(user.getId(), node.getId()));

        mockMvc.perform(post(API_URL + "/" + node.getId() + "/complete"))
                .andExpect(result -> {
                    var response = result.getResponse().getContentAsString();

                    for (var nextNode : node.getOutgoingEdges()) {
                        assertTrue(response.contains(nextNode.getToNode().getId().toString()));
                    }

                    var status = statusRepository.findByIdUserIdAndIdNodeId(user.getId(), node.getId())
                            .orElseThrow();
                    assertEquals(NodeStatus.COMPLETED, status.getStatus());
                });
    }

}
