package com.kiwi.nodes;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.nodes.controllers.NodesController;
import com.kiwi.features.nodes.controllers.NodesService;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDTO;
import com.kiwi.features.nodes.exceptions.NodeLockedException;
import com.kiwi.features.nodes.exceptions.NodeNotFoundException;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.features.users.controllers.UsersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kiwi.nodes.NodesTestFactory.*;
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NodesController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class NodesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean private UsersService usersService;
    @MockitoBean private NodesService nodesService;

    private final String baseAPIUrl = "/api/nodes";



    // ✅ LIST nodes
    @Test
    @WithMockUser(username = "test@test.com")
    public void listNodes_valid() throws Exception {
        when(nodesService.getNodesForUser(1)).thenReturn(List.of(
                dtoNode(1, 1, NodeStatus.OPEN, 100, 0.5f,0.5f),
                dtoNode(2, 2, NodeStatus.LOCKED, 120, 0.7f,0.25f)
        ));

        mockMvc.perform(get(baseAPIUrl + "?userId=1"))
                .andExpect(status().isOk());
    }

    // ✅ UNLOCK node
    @Test
    @WithMockUser(username = "test@test.com")
    public void unlockNode_valid() throws Exception {
        when(nodesService.unlockNode(1, 2))
                .thenReturn(dtoNode(2, 2, NodeStatus.OPEN, 120, 0.5f,0.5f));

        mockMvc.perform(
                getPostRequestBuilder(baseAPIUrl + "/2/unlock", userIdDTO(1))
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void unlockNode_locked() throws Exception {
        when(nodesService.unlockNode(1, 2))
                .thenThrow(new NodeLockedException(2));

        mockMvc.perform(
                getPostRequestBuilder(baseAPIUrl + "/2/unlock", userIdDTO(1))
        ).andExpect(status().isLocked());
    }

    // ✅ COMPLETE node
    @Test
    @WithMockUser(username = "test@test.com")
    public void completeNode_valid() throws Exception {
        when(nodesService.completeNode(1, 2))
                .thenReturn(dtoNode(2, 2, NodeStatus.COMPLETED, 120, 0.15f,0.25f));

        mockMvc.perform(
                getPostRequestBuilder(baseAPIUrl + "/2/complete", userIdDTO(1))
        ).andExpect(status().isOk());
    }
}
