package com.kiwi.goals;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.goals.controllers.GoalController;
import com.kiwi.features.goals.controllers.GoalService;
import com.kiwi.features.goals.data.GoalDTO;
import com.kiwi.features.goals.data.GoalsListDTO;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.goals.exceptions.GoalUnauthorizedException;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
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

import java.time.LocalDate;
import java.util.List;

import static com.kiwi.goals.GoalsTestFactory.*;
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GoalController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class GoalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean private GoalService goalService;

    private final String baseAPIUrl = "/api/user/goals";

    @Test
    @WithMockUser(username = "test@test.com")
    public void createGoals_valid_returnsCreated() throws Exception {
        String date = LocalDate.now().toString();
        GoalsListDTO request = goalsListDTO(date, reviewGoalDTO("goal-1"));
        GoalsListDTO response = goalsListDTO(date, reviewGoalDTO("goal-1"));

        when(goalService.createGoals(any(), any())).thenReturn(response);

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl, request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getGoalsByDate_valid_returnsOk() throws Exception {
        String date = LocalDate.now().toString();
        GoalsListDTO response = goalsListDTO(date, reviewGoalDTO("goal-1"));

        when(goalService.getGoalsByDate(eq(date), any())).thenReturn(response);

        mockMvc.perform(get(baseAPIUrl)
                        .param("date", date)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getAllGoals_valid_returnsOk() throws Exception {
        String date = LocalDate.now().toString();
        List<GoalsListDTO> response = List.of(goalsListDTO(date, reviewGoalDTO("goal-1")));

        when(goalService.getAllGoals(any())).thenReturn(response);

        mockMvc.perform(get(baseAPIUrl + "/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getReviewGoals_valid_returnsOk() throws Exception {
        String date = LocalDate.now().minusDays(1).toString();
        List<GoalsListDTO> response = List.of(goalsListDTO(date, reviewGoalDTO("goal-1")));

        when(goalService.getGoalsToReview(any())).thenReturn(response);

        mockMvc.perform(get(baseAPIUrl + "/review")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void completeGoal_valid_returnsOk() throws Exception {
        String goalId = "goal-1";
        GoalDTO response = completedGoalDTO(goalId);

        when(goalService.completeGoal(eq(goalId), any())).thenReturn(response);

        mockMvc.perform(patch(baseAPIUrl + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void completeGoal_notFound_returnsNotFound() throws Exception {
        String goalId = "non-existent";

        when(goalService.completeGoal(eq(goalId), any()))
                .thenThrow(new GoalNotFoundException(goalId));

        mockMvc.perform(patch(baseAPIUrl + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void completeGoal_unauthorized_returnsForbidden() throws Exception {
        String goalId = "goal-1";

        when(goalService.completeGoal(eq(goalId), any()))
                .thenThrow(new GoalUnauthorizedException("You are not authorized"));

        mockMvc.perform(patch(baseAPIUrl + "/" + goalId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void uncompleteGoal_valid_returnsOk() throws Exception {
        String goalId = "goal-1";
        GoalDTO response = notCompletedGoalDTO(goalId);

        when(goalService.uncompleteGoal(eq(goalId), any())).thenReturn(response);

        mockMvc.perform(patch(baseAPIUrl + "/" + goalId + "/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void uncompleteGoal_notFound_returnsNotFound() throws Exception {
        String goalId = "non-existent";

        when(goalService.uncompleteGoal(eq(goalId), any()))
                .thenThrow(new GoalNotFoundException(goalId));

        mockMvc.perform(patch(baseAPIUrl + "/" + goalId + "/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
