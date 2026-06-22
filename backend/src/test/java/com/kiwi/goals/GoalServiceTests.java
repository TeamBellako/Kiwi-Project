package com.kiwi.goals;

import com.kiwi.features.goals.controllers.GoalService;
import com.kiwi.features.goals.controllers.UserGoalProgressRepository;
import com.kiwi.features.goals.data.*;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.goals.exceptions.GoalUnauthorizedException;
import com.kiwi.features.metrics.controllers.MetricsRepository;
import com.kiwi.features.users.controllers.UserAppUsageRepository;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.kiwi.goals.GoalsTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GoalServiceTests {

    private GoalTestRepositoryInMemory userGoalStatusRepository;
    private GoalDefinitionRepositoryInMemory goalDefinitionRepository;
    private UsersTestRepositoryInMemory usersRepository;
    private UserGoalProgressRepository userGoalProgressRepository;
    private UserAppUsageRepository userAppUsageRepository;
    private MetricsRepository metricsRepository;
    private UsersService usersService;
    private GoalService goalService;

    private UsersPersistence testUser;
    private Authentication authentication;

    @Before
    public void setUp() {
        userGoalStatusRepository = new GoalTestRepositoryInMemory();
        goalDefinitionRepository = new GoalDefinitionRepositoryInMemory();
        usersRepository = new UsersTestRepositoryInMemory();
        userGoalProgressRepository = mock(UserGoalProgressRepository.class);
        userAppUsageRepository = mock(UserAppUsageRepository.class);
        metricsRepository = mock(MetricsRepository.class);
        usersService = mock(UsersService.class);
        goalService = new GoalService(
            userGoalStatusRepository,
            userGoalProgressRepository,
            goalDefinitionRepository,
            usersRepository,
            usersService,
            userAppUsageRepository,
            metricsRepository);

        testUser = new UsersPersistence();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setCurrentPoints(100);
        testUser.setTotalPoints(500);
        usersRepository.save(testUser);

        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    // ============================================================================================
    // CREATE GOALS
    // ============================================================================================

    @Test
    public void createGoals_valid_savesGoals() {
        GoalPersistence def = goalDefinitionRepository.save(exerciseGoalDefinition(null));

        List<UserGoalStatusDTO> request = List.of(
                inProgressGoalDTO(null),
                inProgressGoalDTO(null)
        );
        request.forEach(dto -> dto.setGoalId(def.getId()));

        List<UserGoalStatusDTO> result = goalService.createGoals(request, authentication);

        assertEquals(2, result.size());
        assertEquals(2, userGoalStatusRepository.count());
    }

    @Test
    public void createGoals_addsNewGoalsWithoutReplacing() {
        LocalDate date = LocalDate.now();
        GoalPersistence def = goalDefinitionRepository.save(exerciseGoalDefinition(null));

        userGoalStatusRepository.save(inProgressGoalPersistence(null, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(null, date, testUser));

        assertEquals(2, userGoalStatusRepository.count());

        UserGoalStatusDTO dto = inProgressGoalDTO(null);
        dto.setGoalId(def.getId());
        List<UserGoalStatusDTO> request = List.of(dto);

        List<UserGoalStatusDTO> result = goalService.createGoals(request, authentication);

        assertEquals(1, result.size());
        assertEquals(3, userGoalStatusRepository.count());
    }

    // ============================================================================================
    // GET GOALS
    // ============================================================================================

    @Test
    public void getGoalById_valid_returnsGoal() {
        LocalDate date = LocalDate.now();
        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date, testUser));

        UserGoalStatusDTO result = goalService.getGoalById(1L, authentication);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("IN_PROGRESS", result.getStatus());
    }

    @Test(expected = GoalNotFoundException.class)
    public void getGoalById_notFound_throwsException() {
        goalService.getGoalById(999L, authentication);
    }

    @Test
    public void getGoalsByDate_valid_returnsGoalsForDate() {
        LocalDate date = LocalDate.now();
        LocalDate otherDate = LocalDate.now().minusDays(1);

        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(2L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(3L, otherDate, testUser));

        List<UserGoalStatusDTO> result = goalService.getGoalsByDate(date.toString(), authentication);

        assertEquals(2, result.size());
        assertEquals(date.toString(), result.get(0).getDate());
    }

    @Test
    public void getAllGoals_valid_returnsAllGoalsGroupedByDate() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().minusDays(1);

        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date1, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(2L, date1, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(3L, date2, testUser));

        List<UserGoalStatusDTO> result = goalService.getAllGoals(authentication);

        assertEquals(3, result.size());
        assertEquals(date1.toString(), result.get(0).getDate());
        assertEquals(date1.toString(), result.get(1).getDate());
        assertEquals(date2.toString(), result.get(2).getDate());
    }

    @Test
    public void getGoalsInProgress_valid_returnsOnlyInProgressGoalsBeforeToday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);

        userGoalStatusRepository.save(inProgressGoalPersistence(1L, yesterday, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(2L, twoDaysAgo, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(3L, today, testUser));
        userGoalStatusRepository.save(completedGoalPersistence(4L, yesterday, testUser));

        List<UserGoalStatusDTO> result = goalService.getGoalsInProgress(authentication);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(g -> g.getStatus().equals("IN_PROGRESS")));
    }

    @Test
    public void getAppGoals_valid_returnsOnlyAppUsageGoals() {
        LocalDate date = LocalDate.now();

        userGoalStatusRepository.save(appGoalPersistence(1L, date, testUser));
        userGoalStatusRepository.save(appGoalPersistence(2L, date.minusDays(1), testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(3L, date, testUser));

        List<UserGoalStatusDTO> result = goalService.getAppGoals(authentication);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(g -> g.getCategory().equals("APP_USAGE")));
    }

    @Test
    public void getSkillGoals_valid_returnsOnlySkillGoals() {
        LocalDate date = LocalDate.now();

        userGoalStatusRepository.save(skillGoalPersistence(1L, date, testUser));
        userGoalStatusRepository.save(skillGoalPersistence(2L, date.minusDays(1), testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(3L, date, testUser));

        List<UserGoalStatusDTO> result = goalService.getSkillGoals(authentication);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(g -> g.getCategory().equals("SKILL")));
    }

    @Test
    public void getAppGoals_doesNotReturnOtherUsersGoals() {
        LocalDate date = LocalDate.now();

        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        usersRepository.save(otherUser);

        userGoalStatusRepository.save(appGoalPersistence(1L, date, testUser));
        userGoalStatusRepository.save(appGoalPersistence(2L, date, otherUser));

        List<UserGoalStatusDTO> result = goalService.getAppGoals(authentication);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // ============================================================================================
    // GET GOAL DEFINITIONS (suggestions)
    // ============================================================================================

    @Test
    public void getGoalDefinitions_onlyReturnsDailyChallenges() {
        goalDefinitionRepository.save(exerciseGoalDefinition(null));       // DAILY_CHALLENGES
        goalDefinitionRepository.save(exerciseGoalDefinition(null));       // DAILY_CHALLENGES
        goalDefinitionRepository.save(appGoalDefinition(null));            // APP_USAGE - debe excluirse
        goalDefinitionRepository.save(skillGoalDefinition(null));          // SKILL - debe excluirse

        List<GoalDTO> result = goalService.getGoalDefinitions(authentication);

        assertTrue(result.stream().allMatch(g -> g.getCategory().equals("DAILY_CHALLENGES")));
    }

    @Test
    public void getGoalDefinitions_returnsAtMostTwo() {
        goalDefinitionRepository.save(exerciseGoalDefinition(null));
        goalDefinitionRepository.save(exerciseGoalDefinition(null));
        goalDefinitionRepository.save(exerciseGoalDefinition(null));
        goalDefinitionRepository.save(exerciseGoalDefinition(null));

        List<GoalDTO> result = goalService.getGoalDefinitions(authentication);

        assertTrue(result.size() <= 2);
    }

    // ============================================================================================
    // UPDATE GOAL PROGRESS / UPDATE GOAL — must NEVER auto-complete
    // ============================================================================================

    @Test
    public void updateGoalProgress_incrementCrossesTarget_statusStaysInProgressAndNoPoints() {
        LocalDate date = LocalDate.now();
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        Integer target = entry.getGoal().getTarget();
        entry.setValue(target - 1);
        userGoalStatusRepository.save(entry);

        UserGoalStatusDTO result = goalService.updateGoalProgress(1L, authentication);

        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals(target.intValue(), result.getValue().intValue());
        verify(usersService, never()).addPointsToUser(any(), any());
        verify(userGoalProgressRepository, never()).save(any());
    }

    @Test
    public void updateGoal_setsValueEqualToTarget_statusStaysInProgressAndNoPoints() {
        LocalDate date = LocalDate.now();
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalStatusDTO dto = inProgressGoalDTO(1L);
        dto.setValue(entry.getGoal().getTarget());

        UserGoalStatusDTO result = goalService.updateGoal(1L, dto, authentication);

        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals(entry.getGoal().getTarget().intValue(), result.getValue().intValue());
        verify(usersService, never()).addPointsToUser(any(), any());
        verify(userGoalProgressRepository, never()).save(any());
    }

    // ============================================================================================
    // COMPLETE GOAL
    // ============================================================================================

    @Test
    public void completeGoal_calledTwice_awardsPointsExactlyOnce() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        entry.setValue(entry.getGoal().getTarget());
        userGoalStatusRepository.save(entry);
        when(userGoalProgressRepository.findById(any())).thenReturn(Optional.empty());

        UserGoalStatusDTO first = goalService.completeGoal(1L, authentication);
        UserGoalStatusDTO second = goalService.completeGoal(1L, authentication);

        assertEquals("COMPLETED", first.getStatus());
        assertEquals("COMPLETED", second.getStatus());
        verify(usersService, times(1))
                .addPointsToUser(testUser.getId(), entry.getGoal().getReward());
    }

    @Test
    public void completeGoal_valid_changesStatusAndAddsPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);
        when(userGoalProgressRepository.findById(any())).thenReturn(Optional.empty());

        UserGoalStatusDTO result = goalService.completeGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, times(1)).addPointsToUser(testUser.getId(), entry.getGoal().getReward());
    }

        @Test
        public void completeGoal_updatesProgressAndResetsFailedCounter() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(entry.getGoal().getType().name())
            .build();
        UserGoalProgressPersistence existingProgress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(1)
            .goalsCompletedAtDifficulty(1)
            .goalsFailedAtDifficulty(2)
            .build();

        when(userGoalProgressRepository.findById(progressKey)).thenReturn(Optional.of(existingProgress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        goalService.completeGoal(1L, authentication);

        assertEquals(2, existingProgress.getGoalsCompletedAtDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsFailedAtDifficulty().intValue());
        assertEquals(1, existingProgress.getCurrentDifficulty().intValue());
        verify(userGoalProgressRepository, times(1)).save(existingProgress);
        }

        @Test
        public void completeGoal_reachingThreeCompleted_increasesDifficultyAndResetsCounter() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(entry.getGoal().getType().name())
            .build();
        UserGoalProgressPersistence existingProgress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(2)
            .goalsCompletedAtDifficulty(2)
            .goalsFailedAtDifficulty(1)
            .build();

        when(userGoalProgressRepository.findById(progressKey)).thenReturn(Optional.of(existingProgress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        goalService.completeGoal(1L, authentication);

        assertEquals(3, existingProgress.getCurrentDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsCompletedAtDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsFailedAtDifficulty().intValue());
        }

        @Test
        public void completeThreeTimesThenFailThreeTimes_updatesDifficultyUpAndDown() {
        LocalDate date = LocalDate.now().minusDays(1);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(GoalType.EXERCISE.name())
            .build();
        UserGoalProgressPersistence progress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(1)
            .goalsCompletedAtDifficulty(0)
            .goalsFailedAtDifficulty(0)
            .build();

        when(userGoalProgressRepository.findById(any(UserGoalProgressKey.class)))
            .thenReturn(Optional.of(progress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        userGoalStatusRepository.save(inProgressGoalPersistence(101L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(102L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(103L, date, testUser));

        goalService.completeGoal(101L, authentication);
        goalService.completeGoal(102L, authentication);
        goalService.completeGoal(103L, authentication);

        assertEquals(2, progress.getCurrentDifficulty().intValue());
        assertEquals(0, progress.getGoalsCompletedAtDifficulty().intValue());
        assertEquals(0, progress.getGoalsFailedAtDifficulty().intValue());

        userGoalStatusRepository.save(inProgressGoalPersistence(201L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(202L, date, testUser));
        userGoalStatusRepository.save(inProgressGoalPersistence(203L, date, testUser));

        goalService.uncompleteGoal(201L, authentication);
        goalService.uncompleteGoal(202L, authentication);
        goalService.uncompleteGoal(203L, authentication);

        assertEquals(1, progress.getCurrentDifficulty().intValue());
        assertEquals(0, progress.getGoalsCompletedAtDifficulty().intValue());
        assertEquals(0, progress.getGoalsFailedAtDifficulty().intValue());
        }

    @Test
    public void completeGoal_alreadyCompleted_doesNotAddPoints() {
        LocalDate date = LocalDate.now().minusDays(1);
        userGoalStatusRepository.save(completedGoalPersistence(1L, date, testUser));

        UserGoalStatusDTO result = goalService.completeGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
        verify(usersService, never()).addPointsToUser(any(), any());
    }

    @Test
    public void completeGoal_notInProgress_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        userGoalStatusRepository.save(notCompletedGoalPersistence(1L, date, testUser));

        UserGoalStatusDTO result = goalService.completeGoal(1L, authentication);

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

        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        usersRepository.save(otherUser);

        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date, otherUser));

        goalService.completeGoal(1L, authentication);
    }

    // ============================================================================================
    // UNCOMPLETE GOAL
    // ============================================================================================

    @Test
    public void uncompleteGoal_valid_changesStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date, testUser));
        when(userGoalProgressRepository.findById(any())).thenReturn(Optional.empty());

        UserGoalStatusDTO result = goalService.uncompleteGoal(1L, authentication);

        assertEquals("NOT_COMPLETED", result.getStatus());
    }

        @Test
        public void uncompleteGoal_updatesFailedProgressAndResetsCompletedCounter() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(entry.getGoal().getType().name())
            .build();
        UserGoalProgressPersistence existingProgress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(2)
            .goalsCompletedAtDifficulty(2)
            .goalsFailedAtDifficulty(1)
            .build();

        when(userGoalProgressRepository.findById(progressKey)).thenReturn(Optional.of(existingProgress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        goalService.uncompleteGoal(1L, authentication);

        assertEquals(0, existingProgress.getGoalsCompletedAtDifficulty().intValue());
        assertEquals(2, existingProgress.getGoalsFailedAtDifficulty().intValue());
        assertEquals(2, existingProgress.getCurrentDifficulty().intValue());
        }

        @Test
        public void uncompleteGoal_reachingThreeFailures_decreasesDifficultyAndResetsCounter() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(entry.getGoal().getType().name())
            .build();
        UserGoalProgressPersistence existingProgress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(3)
            .goalsCompletedAtDifficulty(1)
            .goalsFailedAtDifficulty(2)
            .build();

        when(userGoalProgressRepository.findById(progressKey)).thenReturn(Optional.of(existingProgress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        goalService.uncompleteGoal(1L, authentication);

        assertEquals(2, existingProgress.getCurrentDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsFailedAtDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsCompletedAtDifficulty().intValue());
        }

        @Test
        public void uncompleteGoal_atDifficultyOne_doesNotDecreaseBelowOne() {
        LocalDate date = LocalDate.now().minusDays(1);
        UserGoalStatusPersistence entry = inProgressGoalPersistence(1L, date, testUser);
        userGoalStatusRepository.save(entry);

        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
            .userId(testUser.getId())
            .goalType(entry.getGoal().getType().name())
            .build();
        UserGoalProgressPersistence existingProgress = UserGoalProgressPersistence.builder()
            .id(progressKey)
            .currentDifficulty(1)
            .goalsCompletedAtDifficulty(1)
            .goalsFailedAtDifficulty(2)
            .build();

        when(userGoalProgressRepository.findById(progressKey)).thenReturn(Optional.of(existingProgress));
        when(userGoalProgressRepository.save(any(UserGoalProgressPersistence.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        goalService.uncompleteGoal(1L, authentication);

        assertEquals(1, existingProgress.getCurrentDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsFailedAtDifficulty().intValue());
        assertEquals(0, existingProgress.getGoalsCompletedAtDifficulty().intValue());
        }

    @Test
    public void uncompleteGoal_notInProgress_doesNotChangeStatus() {
        LocalDate date = LocalDate.now().minusDays(1);
        userGoalStatusRepository.save(completedGoalPersistence(1L, date, testUser));

        UserGoalStatusDTO result = goalService.uncompleteGoal(1L, authentication);

        assertEquals("COMPLETED", result.getStatus());
    }

    @Test(expected = GoalNotFoundException.class)
    public void uncompleteGoal_nonExistent_throwsException() {
        goalService.uncompleteGoal(999L, authentication);
    }

    @Test(expected = GoalUnauthorizedException.class)
    public void uncompleteGoal_differentUser_throwsException() {
        LocalDate date = LocalDate.now().minusDays(1);

        UsersPersistence otherUser = new UsersPersistence();
        otherUser.setId(2L);
        otherUser.setEmail("other@test.com");
        usersRepository.save(otherUser);

        userGoalStatusRepository.save(inProgressGoalPersistence(1L, date, otherUser));

        goalService.uncompleteGoal(1L, authentication);
    }
}

