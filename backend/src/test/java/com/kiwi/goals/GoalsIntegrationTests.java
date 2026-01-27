package com.kiwi.goals;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.goals.controllers.GoalRepository;
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
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
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
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(dto);
        UsersPersistence user = UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        return usersRepository.saveAndFlush(user);
    }

    // ============================================================
    // TESTS: CREATE GOALS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createGoals_valid_returnsCreated() throws Exception {
        createUser();
        
        List<GoalDTO> request = List.of(
                inProgressGoalDTO(null),
                inProgressGoalDTO(null)
        );

        mockMvc.perform(getPostRequestBuilder(API_URL, request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));        
        goalRepository.flush();    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createGoals_addsNewGoals() throws Exception {
        UsersPersistence user = createUser();
        
        LocalDate date = LocalDate.now();
        
        // Create initial goals (sin IDs manuales)
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.save(inProgressGoalPersistence(null, date, user));
        goalRepository.flush();

        // Add new goals
        List<GoalDTO> request = List.of(
                inProgressGoalDTO(null)
        );

        mockMvc.perform(getPostRequestBuilder(API_URL, request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1));

        goalRepository.flush();
    }

    // ============================================================
    // TESTS: GET GOALS
    // ============================================================


    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getGoalById_valid_returnsGoal() throws Exception {
        UsersPersistence user = createUser();

        LocalDate date = LocalDate.now();
        GoalPersistence goal = inProgressGoalPersistence(null, date, user);
        goal = goalRepository.saveAndFlush(goal);

        mockMvc.perform(get(API_URL + "/" + goal.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goal.getId()))
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getGoalById_notFound_returnsNotFound() throws Exception {
        createUser();

        mockMvc.perform(get(API_URL + "/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value(date.toString()));
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
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].date").value(date1.toString()))
                .andExpect(jsonPath("$[1].date").value(date1.toString()))
                .andExpect(jsonPath("$[2].date").value(date2.toString()));
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
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAppGoals_valid_returnsOnlyAppGoals() throws Exception {
        UsersPersistence user = createUser();

        LocalDate date = LocalDate.now();

        goalRepository.save(appGoalPersistence(null, date, user));
        goalRepository.save(appGoalPersistence(null, date.minusDays(1), user));

        goalRepository.save(inProgressGoalPersistence(null, date, user));

        goalRepository.flush();

        mockMvc.perform(get(API_URL + "/app_usage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("APP_USAGE"))
                .andExpect(jsonPath("$[1].category").value("APP_USAGE"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAppGoals_noAppGoals_returnsEmptyList() throws Exception {
        UsersPersistence user = createUser();

        goalRepository.save(inProgressGoalPersistence(null, LocalDate.now(), user));
        goalRepository.flush();

        mockMvc.perform(get(API_URL + "/app_usage")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
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
        Long goalId = goal.getId();

        mockMvc.perform(patch(API_URL + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Verify points were added
        UsersPersistence updatedUser = usersRepository.findById(user.getId()).orElseThrow();
        assert updatedUser.getCurrentPoints() == initialPoints + goal.getReward();
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void completeGoal_notFound_returnsNotFound() throws Exception {
        createUser();

        mockMvc.perform(patch(API_URL + "/99999/complete")
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
        Long goalId = goal.getId();

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
        Long goalId = goal.getId();

        mockMvc.perform(patch(API_URL + "/" + goalId + "/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOT_COMPLETED"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void uncompleteGoal_notFound_returnsNotFound() throws Exception {
        createUser();

        mockMvc.perform(patch(API_URL + "/99999/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
