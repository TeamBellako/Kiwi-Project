package com.kiwi.nodes;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.nodes.controllers.NodesController;
import com.kiwi.features.nodes.controllers.NodesService;
import com.kiwi.features.nodes.data.NodeStatus;
import com.kiwi.features.nodes.data.NodesDTO;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.features.users.controllers.UsersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @WithMockUser(username = "test@test.com")
    public void listNodes_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                    setEmail("test@test.com");
                }}));

        when(nodesService.getNodesForUser(1L))
                .thenReturn(List.of(
                        new NodesDTO(1L, NodeStatus.OPEN.name(), 0, 100, 0.5f, 0.5f, "node1", "Node 1", List.of(2L), 0, true, "", 0),
                        new NodesDTO(2L, NodeStatus.LOCKED.name(), 0, 120, 0.7f, 0.25f, "node2", "Node 2", List.of(), 0, false, "", 0)
                ));

        mockMvc.perform(get(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void unlockNode_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                    setEmail("test@test.com");
                }}));

        when(nodesService.unlockNode(1L, 1L))
                .thenReturn(new NodesDTO(1L, NodeStatus.OPEN.name(), 0, 100, 0.5f, 0.5f, "node1", "Node 1", List.of(2L), 0, true, "", 0));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/unlock", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void completeNode_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                    setEmail("test@test.com");
                }}));

        when(nodesService.completeNode(1L, 2L))
                .thenReturn(List.of(new NodesDTO(3L, NodeStatus.LOCKED.name(), 0, 150, 0.8f, 0.4f, "node3", "Node 3", List.of(),0, false, "", 0)));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/2/complete", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
