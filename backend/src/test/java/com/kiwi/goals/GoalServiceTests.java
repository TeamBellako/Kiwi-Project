package com.kiwi.goals;

import com.kiwi.features.goals.controllers.GoalService;
import com.kiwi.features.goals.data.*;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.goals.exceptions.GoalUnauthorizedException;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static com.kiwi.goals.GoalsTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GoalServiceTests {

    private GoalTestRepositoryInMemory goalRepository;
    private UsersTestRepositoryInMemory usersRepository;
    private UsersService usersService;
    private GoalService goalService;

    private UsersPersistence testUser;
    private Authentication authentication;

    @Before
    public void setUp() {
        goalRepository = new GoalTestRepositoryInMemory();
        usersRepository = new UsersTestRepositoryInMemory();
        usersService = mock(UsersService.class);
        goalService = new GoalService(goalRepository, usersRepository, usersService);

        // Create test user
        testUser = new UsersPersistence();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setCurrentPoints(100);
        testUser.setTotalPoints(500);
        usersRepository.save(testUser);

        // Mock authentication
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    // ============================================================================================
    // CREATE GOALS
    // ============================================================================================

    @Test
    public void createGoals_valid_savesGoals() {
        List<GoalDTO> request = List.of(
                inProgressGoalDTO(null),
                inProgressGoalDTO(null)
        );

        List<GoalDTO> result = goalService.createGoals(request, authentication);

        assertEquals(2, result.size());
        assertEquals(2, goalRepository.count());
    }

    @Test
    public void createGoals_addsNewGoalsWithoutReplacing() {
        LocalDate date = LocalDate.now();
        
        // Create initial goals without explicit IDs
        goalRepository.save(inProgressGoalPersistence(null, date, testUser));
        goalRepository.save(inProgressGoalPersistence(null, date, testUser));

        assertEquals(2, goalRepository.count());

        // Create new goals for same date
        List<GoalDTO> request = List.of(
                inProgressGoalDTO(null)
        );

        List<GoalDTO> result = goalService.createGoals(request, authentication);

        assertEquals(1, result.size());
        assertEquals(3, goalRepository.count());
    }

    // ============================================================================================
    // GET GOALS
    // ============================================================================================

    @Test
    public void getGoalsByDate_valid_returnsGoalsForDate() {
        LocalDate date = LocalDate.now();
        LocalDate otherDate = LocalDate.now().minusDays(1);

        goalRepository.save(inProgressGoalPersistence(1L, date, testUser));
        goalRepository.save(inProgressGoalPersistence(2L, date, testUser));
        goalRepository.save(inProgressGoalPersistence(3L, otherDate, testUser));

        List<GoalDTO> result = goalService.getGoalsByDate(date.toString(), authentication);

        assertEquals(2, result.size());
        assertEquals(date.toString(), result.get(0).getDate());
    }

    @Test
    public void getAllGoals_valid_returnsAllGoalsGroupedByDate() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().minusDays(1);

        goalRepository.save(inProgressGoalPersistence(1L, date1, testUser));
        goalRepository.save(inProgressGoalPersistence(2L, date1, testUser));
        goalRepository.save(inProgressGoalPersistence(3L, date2, testUser));

        List<GoalDTO> result = goalService.getAllGoals(authentication);

        assertEquals(3, result.size());
        
        // Should be sorted descending by date
        assertEquals(date1.toString(), result.get(0).getDate());
        assertEquals(date1.toString(), result.get(1).getDate());
        assertEquals(date2.toString(), result.get(2).getDate());
    }

    @Test
    public void getGoalsInProgress_valid_returnsOnlyInProgressGoalsBeforeToday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        // IN_PROGRESS goals from yesterday and two days ago
        goalRepository.save(inProgressGoalPersistence(1L, yesterday, testUser));
        goalRepository.save(inProgressGoalPersistence(2L, twoDaysAgo, testUser));
        
        // IN_PROGRESS goal from today (should NOT be included)
        goalRepository.save(inProgressGoalPersistence(3L, today, testUser));
        
        // COMPLETED goal from yesterday (should NOT be included)
        goalRepository.save(completedGoalPersistence(4L, yesterday, testUser));

        List<GoalDTO> result = goalService.getGoalsInProgress(authentication);

        assertEquals(2, result.size());
        // Verify they are IN_PROGRESS and from before today
        assertTrue(result.stream().allMatch(g -> g.getStatus().equals("IN_PROGRESS")));
    }

    // ============================================================================================
    // COMPLETE GOAL
    // ============================================================================================

    @Test
    public void completeGoal_valid_changesStatusAndAddsPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = inProgressGoalPersistence(1L, date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, times(1)).addPointsToUser(testUser.getId(), goal.getReward());
    }

    @Test
    public void completeGoal_alreadyCompleted_doesNotAddPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = completedGoalPersistence(1L, date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, never()).addPointsToUser(any(), any());
    }

    @Test
    public void completeGoal_notInProgress_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = notCompletedGoalPersistence(1L, date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal(1L, authentication);

        assertEquals("NOT_COMPLETED", result.getStatus());
        verify(usersService, never()).addPointsToUser(any(), any());
    }

    @Test(expected = GoalNotFoundException.class)
    public void completeGoal_nonExistent_throwsException() {
        goalService.completeGoal(999L, authentication);
    }

    @Test(expected = GoalUnauthorizedException.class)
    public void completeGoal_differentUser_throwsException() {
        LocalDate date = LocalDate.now().minusDays(1);
        
        // Create another user
        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        usersRepository.save(otherUser);

        // Create goal for other user
        GoalPersistence goal = inProgressGoalPersistence(1L, date, otherUser);
        goalRepository.save(goal);

        // Try to complete with current user
        goalService.completeGoal(1L, authentication);
    }

    // ============================================================================================
    // UNCOMPLETE GOAL
    // ============================================================================================

    @Test
    public void uncompleteGoal_valid_changesStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = inProgressGoalPersistence(1L, date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.uncompleteGoal(1L, authentication);

        assertEquals("NOT_COMPLETED", result.getStatus());
    }

    @Test
    public void uncompleteGoal_notInProgress_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = completedGoalPersistence(1L, date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.uncompleteGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test(expected = GoalNotFoundException.class)
    public void uncompleteGoal_nonExistent_throwsException() {
        goalService.uncompleteGoal(999L, authentication);
    }

    @Test(expected = GoalUnauthorizedException.class)
    public void uncompleteGoal_differentUser_throwsException() {
        LocalDate date = LocalDate.now().minusDays(1);
        
        // Create another user
        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        usersRepository.save(otherUser);

        // Create goal for other user
        GoalPersistence goal = inProgressGoalPersistence(1L, date, otherUser);
        goalRepository.save(goal);

        // Try to uncomplete with current user
        goalService.uncompleteGoal(1L, authentication);
    }
}
