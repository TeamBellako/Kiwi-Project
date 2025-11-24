package com.kiwi.quests;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.quests.controllers.QuestRepository;
import com.kiwi.features.quests.controllers.SubquestRepository;
import com.kiwi.features.quests.controllers.UserQuestStatusRepository;
import com.kiwi.features.quests.controllers.UserSubquestStatusRepository;
import com.kiwi.features.quests.data.QuestPersistence;
import com.kiwi.features.quests.data.QuestStatus;
import com.kiwi.features.quests.data.SubquestPersistence;
import com.kiwi.features.quests.data.SubquestStatus;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kiwi.quests.QuestTestFactory.*;
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
public class QuestsIntegrationTests {

    private final String API_URL = "/api/quests";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UsersRepository usersRepository;
    @Autowired private QuestRepository questRepository;
    @Autowired private SubquestRepository subquestRepository;
    @Autowired private UserQuestStatusRepository questStatusRepository;
    @Autowired private UserSubquestStatusRepository subquestStatusRepository;

    @Autowired private JdbcTemplate jdbc;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser() {
        var dto = validUserDTO();
        UsersDomain domain = UsersDataMapper.toDomain(dto);
        UsersPersistence user = UsersDataMapper.toPersistence(domain, dto.getPassword());
        return usersRepository.saveAndFlush(user);
    }

    private QuestPersistence createQuestWithSubquests(int questId, int... subquestIds) {
        var q = quest(questId);
        questRepository.saveAndFlush(q);

        int order = 1;
        for (int sid : subquestIds) {
            var s = subquest(sid, q, order++);
            subquestRepository.saveAndFlush(s);
        }
        return q;
    }

    // ============================================================
    // TESTS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getActiveQuests() throws Exception {
        var user = createUser();

        var q1 = createQuestWithSubquests(1, 1);
        var q2 = createQuestWithSubquests(2, 2);
        var q3 = createQuestWithSubquests(4, 3);

        questStatusRepository.saveAndFlush(activeQuestStatus(user.getId(), q1));
        questStatusRepository.saveAndFlush(activeQuestStatus(user.getId(), q2));

        mockMvc.perform(get(API_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getCompletedQuests() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(1, 4);
        questStatusRepository.saveAndFlush(completedQuestStatus(user.getId(), quest));

        mockMvc.perform(get(API_URL + "/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void giveQuest() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(3, 5, 6);

        mockMvc.perform(post(API_URL + "/" + quest.getId() + "/give"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questId").value(quest.getId()))
                .andExpect(jsonPath("$.status").value(QuestStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.subquests[0].status").value(SubquestStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.subquests[0].order").value(1))
                .andExpect(jsonPath("$.subquests[1].status").value(SubquestStatus.LOCKED.toString()))
                .andExpect(jsonPath("$.subquests[1].order").value(2));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeSubquest() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(1, 7, 8);
        List<SubquestPersistence> subquests = subquestRepository.findAllByQuestIdOrderByOrderIndex(quest.getId());

        mockMvc.perform(post(API_URL + "/" + quest.getId() + "/give"));

        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(0).getId() + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedSubquest.subquestId").value( subquests.get(0).getId()))
                .andExpect(jsonPath("$.updatedSubquest.status").value(SubquestStatus.COMPLETED.toString()))
                .andExpect(jsonPath("$.nextSubquest.subquestId").value( subquests.get(1).getId()))
                .andExpect(jsonPath("$.nextSubquest.status").value(SubquestStatus.ACTIVE.toString()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void failSubquest() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(1, 7, 8);
        List<SubquestPersistence> subquests = subquestRepository.findAllByQuestIdOrderByOrderIndex(quest.getId());

        mockMvc.perform(post(API_URL + "/" + quest.getId() + "/give"));

        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(0).getId() + "/fail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedSubquest.subquestId").value( subquests.get(0).getId()))
                .andExpect(jsonPath("$.updatedSubquest.status").value(SubquestStatus.FAILED.toString()))
                .andExpect(jsonPath("$.nextSubquest.subquestId").value( subquests.get(1).getId()))
                .andExpect(jsonPath("$.nextSubquest.status").value(SubquestStatus.ACTIVE.toString()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeLastSubquest() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(1, 7, 8);
        List<SubquestPersistence> subquests = subquestRepository.findAllByQuestIdOrderByOrderIndex(quest.getId());

        mockMvc.perform(post(API_URL + "/" + quest.getId() + "/give"));
        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(0).getId() + "/complete"));

        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(1).getId() + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedSubquest.subquestId").value( subquests.get(1).getId()))
                .andExpect(jsonPath("$.updatedSubquest.status").value(SubquestStatus.COMPLETED.toString()))
                .andExpect(jsonPath("$.completedQuest.questId").value( quest.getId()))
                .andExpect(jsonPath("$.completedQuest.status").value(QuestStatus.COMPLETED.toString()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void faiLastSubquest() throws Exception {
        var user = createUser();

        var quest = createQuestWithSubquests(1, 7, 8);
        List<SubquestPersistence> subquests = subquestRepository.findAllByQuestIdOrderByOrderIndex(quest.getId());

        mockMvc.perform(post(API_URL + "/" + quest.getId() + "/give"));
        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(0).getId() + "/complete"));

        mockMvc.perform(post(API_URL + "/subquests/" +  subquests.get(1).getId() + "/fail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updatedSubquest.subquestId").value( subquests.get(1).getId()))
                .andExpect(jsonPath("$.updatedSubquest.status").value(SubquestStatus.FAILED.toString()))
                .andExpect(jsonPath("$.completedQuest.questId").value( quest.getId()))
                .andExpect(jsonPath("$.completedQuest.status").value(QuestStatus.COMPLETED.toString()));
    }
}
