package com.kiwi.quests;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.quests.controllers.QuestController;
import com.kiwi.features.quests.controllers.QuestService;
import com.kiwi.features.quests.data.QuestDTO;
import com.kiwi.features.quests.data.SubquestResultDTO;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
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
@WebMvcTest(QuestController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class QuestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean private UsersService usersService;
    @MockitoBean private QuestService questService;

    private final String baseAPIUrl = "/api/quests";

    @Test
    @WithMockUser(username = "test@test.com")
    public void getActiveQuests_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1);
                    setEmail("test@test.com");
                }}));

        when(questService.getActiveQuestsForUser(1))
                .thenReturn(List.of(new QuestDTO(), new QuestDTO()));

        mockMvc.perform(get(baseAPIUrl + "/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getCompletedQuests_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1);
                    setEmail("test@test.com");
                }}));

        when(questService.getCompletedQuestsForUser(1))
                .thenReturn(List.of(new QuestDTO()));

        mockMvc.perform(get(baseAPIUrl + "/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void giveQuest_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1);
                    setEmail("test@test.com");
                }}));

        when(questService.giveQuestToUser(1, 1)).thenReturn(new QuestDTO());

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/give", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void completeSubquest_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1);
                    setEmail("test@test.com");
                }}));

        when(questService.completeSubquest(1, 1))
                .thenReturn(SubquestResultDTO.builder().build());

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/subquests/1/complete", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void failSubquest_valid_returnsOk() throws Exception {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1);
                    setEmail("test@test.com");
                }}));

        when(questService.failSubquest(1, 1))
                .thenReturn(SubquestResultDTO.builder().build());

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/subquests/1/fail", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
