package com.kiwi.goals;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.goals.data.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.kiwi.goals.GoalsTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
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
public class GoalsIntegrationTests {

    private final String API_URL = "/api/user/goals";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsersRepository usersRepository;
    @Autowired private GoalRepository goalRepository;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser() {
        var dto = validUserDTO();
        UsersDomain domain = UsersDataMapper.toDomain(dto);
        UsersPersistence user = UsersDataMapper.toPersistence(domain, dto.getPassword());
        return usersRepository.saveAndFlush(user);
    }

    // ============================================================
    // TESTS: CREATE GOALS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createGoals_valid_returnsCreated() throws Exception {
        createUser();
        
        LocalDate date = LocalDate.now();
        GoalsListDTO request = goalsListDTO(
                date.toString(),
                inProgressGoalDTO(null),
                inProgressGoalDTO(null)
        );

        mockMvc.perform(getPostRequestBuilder(API_URL, request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.goals.length()").value(2));        
        goalRepository.flush();    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createGoals_replacesExistingGoals() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate date = LocalDate.now();
        
        // Create initial goals (sin IDs manuales)
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.flush();

        // Replace with new goals
        GoalsListDTO request = goalsListDTO(
                date.toString(),
                inProgressGoalDTO(null)
        );

        mockMvc.perform(getPostRequestBuilder(API_URL, request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goals.length()").value(1));

        goalRepository.flush();
        
        // Verify old goals are deleted
        List<GoalPersistence> remaining = goalRepository.findByUserAndDate(user, date);
        assert remaining.size() == 1;
    }

    // ============================================================
    // TESTS: GET GOALS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getGoalsByDate_valid_returnsGoals() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate date = LocalDate.now();
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.flush();

        mockMvc.perform(get(API_URL)
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.goals.length()").value(2));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAllGoals_valid_returnsAllGoalsGroupedByDate() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().minusDays(1);

        goalRepository.save(inProgressGoalPersistence(null, date1, user));
        goalRepository.save(inProgressGoalPersistence(null, date1, user));
        goalRepository.save(inProgressGoalPersistence(null, date2, user));
        goalRepository.flush();

        mockMvc.perform(get(API_URL + "/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value(date1.toString()))
                .andExpect(jsonPath("$[0].goals.length()").value(2))
                .andExpect(jsonPath("$[1].date").value(date2.toString()))
                .andExpect(jsonPath("$[1].goals.length()").value(1));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getGoalsInProgress_valid_returnsOnlyInProgressGoalsBeforeToday() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        // IN_PROGRESS goals from yesterday (should be included)
        goalRepository.save(inProgressGoalPersistence(null, yesterday, user));
        
        // IN_PROGRESS goal from today (should NOT be included)
        goalRepository.save(inProgressGoalPersistence(null, today, user));
        
        // COMPLETED goal from yesterday (should NOT be included)
        goalRepository.save(completedGoalPersistence(null, yesterday, user));
        goalRepository.flush();

        mockMvc.perform(get(API_URL + "/in_progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].date").value(yesterday.toString()))
                .andExpect(jsonPath("$[0].goals.length()").value(1))
                .andExpect(jsonPath("$[0].goals[0].status").value("IN_PROGRESS"));
    }

    // ============================================================
    // TESTS: COMPLETE GOAL
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeGoal_valid_changesStatusAndAddsPoints() throws Exception {
        UsersPersistence user = createUser();
        int initialPoints = user.getCurrentPoints();
        
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = inProgressGoalPersistence(null, date, user);
        goal = goalRepository.saveAndFlush(goal);
        String goalId = goal.getId();

        mockMvc.perform(patch(API_URL + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Verify points were added
        UsersPersistence updatedUser = usersRepository.findById(user.getId()).orElseThrow();
        assert updatedUser.getCurrentPoints() == initialPoints + goal.getPoints();
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeGoal_notFound_returnsNotFound() throws Exception {
        createUser();

        mockMvc.perform(patch(API_URL + "/non-existent/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeGoal_differentUser_returnsForbidden() throws Exception {
        UsersPersistence user = createUser();
        
        // Create another user
        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setEmail("other@test.com");
        otherUser.setHashedPassword("password");
        otherUser.setCurrentPoints(100);
        otherUser.setTotalPoints(100);
        otherUser.setRegisterDate(LocalDate.now());
        otherUser = usersRepository.saveAndFlush(otherUser);
        
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = inProgressGoalPersistence(null, date, otherUser);
        goal = goalRepository.saveAndFlush(goal);
        String goalId = goal.getId();

        mockMvc.perform(patch(API_URL + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // TESTS: UNCOMPLETE GOAL
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void uncompleteGoal_valid_changesStatus() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = inProgressGoalPersistence(null, date, user);
        goal = goalRepository.saveAndFlush(goal);
        String goalId = goal.getId();

        mockMvc.perform(patch(API_URL + "/" + goalId + "/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_COMPLETED"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void uncompleteGoal_notFound_returnsNotFound() throws Exception {
        createUser();

        mockMvc.perform(patch(API_URL + "/non-existent/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
