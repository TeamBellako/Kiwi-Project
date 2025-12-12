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
        LocalDate date = LocalDate.now();
        GoalsListDTO request = goalsListDTO(
                date.toString(),
                reviewGoalDTO(null),
                reviewGoalDTO(null)
        );

        GoalsListDTO result = goalService.createGoals(request, authentication);

        assertEquals(date.toString(), result.getDate());
        assertEquals(2, result.getGoals().size());
        assertEquals(2, goalRepository.count());
    }

    @Test
    public void createGoals_replacesExistingGoalsForDate() {
        LocalDate date = LocalDate.now();
        
        // Create initial goals
        goalRepository.save(reviewGoalPersistence("goal-1", date, testUser));
        goalRepository.save(reviewGoalPersistence("goal-2", date, testUser));

        assertEquals(2, goalRepository.count());

        // Create new goals for same date
        GoalsListDTO request = goalsListDTO(
                date.toString(),
                reviewGoalDTO(null)
        );

        GoalsListDTO result = goalService.createGoals(request, authentication);

        assertEquals(1, result.getGoals().size());
        assertEquals(1, goalRepository.count());
    }

    // ============================================================================================
    // GET GOALS
    // ============================================================================================

    @Test
    public void getGoalsByDate_valid_returnsGoalsForDate() {
        LocalDate date = LocalDate.now();
        LocalDate otherDate = LocalDate.now().minusDays(1);

        goalRepository.save(reviewGoalPersistence("goal-1", date, testUser));
        goalRepository.save(reviewGoalPersistence("goal-2", date, testUser));
        goalRepository.save(reviewGoalPersistence("goal-3", otherDate, testUser));

        GoalsListDTO result = goalService.getGoalsByDate(date.toString(), authentication);

        assertEquals(date.toString(), result.getDate());
        assertEquals(2, result.getGoals().size());
    }

    @Test
    public void getAllGoals_valid_returnsAllGoalsGroupedByDate() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().minusDays(1);

        goalRepository.save(reviewGoalPersistence("goal-1", date1, testUser));
        goalRepository.save(reviewGoalPersistence("goal-2", date1, testUser));
        goalRepository.save(reviewGoalPersistence("goal-3", date2, testUser));

        List<GoalsListDTO> result = goalService.getAllGoals(authentication);

        assertEquals(2, result.size());
        
        // Should be sorted descending by date
        assertEquals(date1.toString(), result.get(0).getDate());
        assertEquals(2, result.get(0).getGoals().size());
        assertEquals(date2.toString(), result.get(1).getDate());
        assertEquals(1, result.get(1).getGoals().size());
    }

    @Test
    public void getGoalsToReview_valid_returnsOnlyReviewGoalsBeforeToday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        // REVIEW goals from yesterday and two days ago
        goalRepository.save(reviewGoalPersistence("goal-1", yesterday, testUser));
        goalRepository.save(reviewGoalPersistence("goal-2", twoDaysAgo, testUser));
        
        // REVIEW goal from today (should NOT be included)
        goalRepository.save(reviewGoalPersistence("goal-3", today, testUser));
        
        // COMPLETED goal from yesterday (should NOT be included)
        goalRepository.save(completedGoalPersistence("goal-4", yesterday, testUser));

        List<GoalsListDTO> result = goalService.getGoalsToReview(authentication);

        assertEquals(2, result.size());
        assertEquals(yesterday.toString(), result.get(0).getDate());
        assertEquals(1, result.get(0).getGoals().size());
        assertEquals(twoDaysAgo.toString(), result.get(1).getDate());
        assertEquals(1, result.get(1).getGoals().size());
    }

    // ============================================================================================
    // COMPLETE GOAL
    // ============================================================================================

    @Test
    public void completeGoal_valid_changesStatusAndAddsPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = reviewGoalPersistence("goal-1", date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal("goal-1", authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, times(1)).addPointsToUser(testUser.getId(), goal.getPoints());
    }

    @Test
    public void completeGoal_alreadyCompleted_doesNotAddPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = completedGoalPersistence("goal-1", date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal("goal-1", authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, never()).addPointsToUser(any(), any());
    }

    @Test
    public void completeGoal_notInReview_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = notCompletedGoalPersistence("goal-1", date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.completeGoal("goal-1", authentication);

        assertEquals("NOT_COMPLETED", result.getStatus());
        verify(usersService, never()).addPointsToUser(any(), any());
    }

    @Test(expected = GoalNotFoundException.class)
    public void completeGoal_nonExistent_throwsException() {
        goalService.completeGoal("non-existent", authentication);
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
        GoalPersistence goal = reviewGoalPersistence("goal-1", date, otherUser);
        goalRepository.save(goal);

        // Try to complete with current user
        goalService.completeGoal("goal-1", authentication);
    }

    // ============================================================================================
    // UNCOMPLETE GOAL
    // ============================================================================================

    @Test
    public void uncompleteGoal_valid_changesStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = reviewGoalPersistence("goal-1", date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.uncompleteGoal("goal-1", authentication);

        assertEquals("NOT_COMPLETED", result.getStatus());
    }

    @Test
    public void uncompleteGoal_notInReview_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        GoalPersistence goal = completedGoalPersistence("goal-1", date, testUser);
        goalRepository.save(goal);

        GoalDTO result = goalService.uncompleteGoal("goal-1", authentication);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test(expected = GoalNotFoundException.class)
    public void uncompleteGoal_nonExistent_throwsException() {
        goalService.uncompleteGoal("non-existent", authentication);
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
        GoalPersistence goal = reviewGoalPersistence("goal-1", date, otherUser);
        goalRepository.save(goal);

        // Try to uncomplete with current user
        goalService.uncompleteGoal("goal-1", authentication);
    }
}
