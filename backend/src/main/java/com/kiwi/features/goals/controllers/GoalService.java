package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.*;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.goals.exceptions.GoalUnauthorizedException;
import com.kiwi.features.metrics.controllers.MetricsRepository;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.data.AppUsageType;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.controllers.UserAppUsageRepository;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoalService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final UserGoalStatusRepository userGoalStatusRepository;
    private final UserGoalProgressRepository userGoalProgressRepository;
    private final GoalRepository goalRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final UserAppUsageRepository userAppUsageRepository;
    private final MetricsRepository metricsRepository;

    public GoalService(
            UserGoalStatusRepository userGoalStatusRepository,
            UserGoalProgressRepository userGoalProgressRepository,
            GoalRepository goalRepository,
            UsersRepository usersRepository,
            UsersService usersService,
            UserAppUsageRepository userAppUsageRepository,
            MetricsRepository metricsRepository) {
        this.userGoalStatusRepository = userGoalStatusRepository;
        this.userGoalProgressRepository = userGoalProgressRepository;
        this.goalRepository = goalRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
        this.userAppUsageRepository = userAppUsageRepository;
        this.metricsRepository = metricsRepository;
    }

    private void updateUserGoalProgressAfterCompletion(UsersPersistence user, GoalPersistence goal) {
        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
                .userId(user.getId())
                .goalType(goal.getType().name())
                .build();

        UserGoalProgressPersistence progress = userGoalProgressRepository.findById(progressKey)
                .orElseGet(() -> UserGoalProgressPersistence.builder()
                        .id(progressKey)
                        .currentDifficulty(1)
                        .goalsCompletedAtDifficulty(0)
                        .goalsFailedAtDifficulty(0)
                        .build());

        progress.setGoalsCompletedAtDifficulty(progress.getGoalsCompletedAtDifficulty() + 1);
        progress.setGoalsFailedAtDifficulty(0);

        if (progress.getGoalsCompletedAtDifficulty() >= 3) {
            progress.setCurrentDifficulty(progress.getCurrentDifficulty() + 1);
            progress.setGoalsCompletedAtDifficulty(0);
        }

        userGoalProgressRepository.save(progress);
    }

    private void updateUserGoalProgressAfterFailure(UsersPersistence user, GoalPersistence goal) {
        UserGoalProgressKey progressKey = UserGoalProgressKey.builder()
                .userId(user.getId())
                .goalType(goal.getType().name())
                .build();

        UserGoalProgressPersistence progress = userGoalProgressRepository.findById(progressKey)
                .orElseGet(() -> UserGoalProgressPersistence.builder()
                        .id(progressKey)
                        .currentDifficulty(1)
                        .goalsCompletedAtDifficulty(0)
                        .goalsFailedAtDifficulty(0)
                        .build());

        progress.setGoalsFailedAtDifficulty(progress.getGoalsFailedAtDifficulty() + 1);
        progress.setGoalsCompletedAtDifficulty(0);

        if (progress.getGoalsFailedAtDifficulty() >= 3) {
            progress.setCurrentDifficulty(Math.max(1, progress.getCurrentDifficulty() - 1));
            progress.setGoalsFailedAtDifficulty(0);
        }

        userGoalProgressRepository.save(progress);
    }

    private UsersPersistence getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersNotFoundException(email));
    }

    /**
     * Computes the dynamic target (in ms) for AppUsage goal types, or null for
     * standard goals whose target is read from the goal template.
     */
    private Integer resolveTargetOverride(UsersPersistence user, GoalPersistence goal) {
        GoalType type = goal.getType();
        if (type == GoalType.APP_USAGE_GOOD) {
            long baseline = userAppUsageRepository.findByUserAndAppType(user, AppUsageType.GOOD)
                    .map(u -> u.getAvgDailyUsageMs()).orElse(0L);
            int difficulty = userGoalProgressRepository
                    .findById(new UserGoalProgressKey(user.getId(), GoalType.APP_USAGE_GOOD.name()))
                    .map(UserGoalProgressPersistence::getCurrentDifficulty).orElse(1);
            return (int) AppUsageGoalTargetCalculator.computeGoodAppTarget(baseline, difficulty);
        }
        if (type == GoalType.APP_USAGE_BAD) {
            long baseline = userAppUsageRepository.findByUserAndAppType(user, AppUsageType.BAD)
                    .map(u -> u.getAvgDailyUsageMs()).orElse(0L);
            int difficulty = userGoalProgressRepository
                    .findById(new UserGoalProgressKey(user.getId(), GoalType.APP_USAGE_BAD.name()))
                    .map(UserGoalProgressPersistence::getCurrentDifficulty).orElse(1);
            return (int) AppUsageGoalTargetCalculator.computeBadAppTarget(baseline, difficulty);
        }
        return null;
    }

    @Transactional
    public UserGoalStatusDTO updateGoalProgress(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence existing = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        Integer target = existing.getGoal().getTarget();
        Integer increment = Math.max(1, target / 10);
        Integer newValue = Math.min(existing.getValue() + increment, target);
        existing.setValue(newValue);

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(existing));
    }

    @Transactional
    public UserGoalStatusDTO updateGoal(Long id, UserGoalStatusDTO dto, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence existing = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        existing.setValue(dto.getValue());

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(existing));
    }

    @Transactional
    public List<UserGoalStatusDTO> createGoals(List<UserGoalStatusDTO> dtos, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        LocalDate defaultDate = LocalDate.now();
        final LocalDate date;
        if (!dtos.isEmpty() && dtos.get(0).getDate() != null && !dtos.get(0).getDate().isEmpty()) {
            date = LocalDate.parse(dtos.get(0).getDate(), DATE_FORMATTER);
        } else {
            date = defaultDate;
        }

        List<UserGoalStatusPersistence> newEntities = dtos.stream()
                .map(dto -> {
                    dto.setId(null);
                    GoalPersistence goal = goalRepository.findById(dto.getGoalId())
                            .orElseThrow(() -> new GoalNotFoundException(dto.getGoalId()));
                    Integer targetOverride = resolveTargetOverride(user, goal);
                    return UserGoalStatusDataMapper.toEntity(dto, user, goal, date, targetOverride);
                })
                .collect(Collectors.toList());

        return userGoalStatusRepository.saveAll(newEntities).stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca un goal como completado y añade puntos al usuario.
     * SOLO se puede marcar como completado un goal en estado IN_PROGRESS.
     * SOLO puede completar el propietario del goal.
     */
    @Transactional
    public UserGoalStatusDTO completeGoal(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to complete this goal");
        }

        if (entry.getStatus() != GoalStatus.IN_PROGRESS) {
            return UserGoalStatusDataMapper.toDTO(entry);
        }

        usersService.addPointsToUser(user.getId(), entry.getGoal().getReward());
        updateUserGoalProgressAfterCompletion(user, entry.getGoal());

        entry.setStatus(GoalStatus.COMPLETED);
        entry.setValue(entry.getGoal().getTarget());

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(entry));
    }

    /**
     * Marca un goal como no completado.
     * SOLO se puede marcar como no completado un goal en estado IN_PROGRESS.
     * SOLO puede marcarlo el propietario del goal.
     */
    @Transactional
    public UserGoalStatusDTO uncompleteGoal(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to uncomplete this goal");
        }

        if (entry.getStatus() != GoalStatus.IN_PROGRESS) {
            return UserGoalStatusDataMapper.toDTO(entry);
        }

        updateUserGoalProgressAfterFailure(user, entry.getGoal());
        entry.setStatus(GoalStatus.NOT_COMPLETED);

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.saveAndFlush(entry));
    }

    // region GETTERS
    public UserGoalStatusDTO getGoalById(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GoalNotFoundException(id));

        return UserGoalStatusDataMapper.toDTO(entry);
    }

    public List<UserGoalStatusDTO> getGoalsByDate(String dateString, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

        return userGoalStatusRepository
                .findByUserAndDateAndGoal_Category(user, date, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .toList();
    }

    public List<UserGoalStatusDTO> getAllGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return userGoalStatusRepository
                .findByUserAndGoal_CategoryOrderByDateDesc(user, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getGoalsInProgress(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate today = LocalDate.now();

        return userGoalStatusRepository
                .findByUserAndStatusAndDateBeforeAndGoal_CategoryOrderByDateDesc(
                        user, GoalStatus.IN_PROGRESS, today, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getAppGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return userGoalStatusRepository
                .findByUserAndGoal_Category(user, GoalCategory.APP_USAGE)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getSkillGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        List<UserGoalStatusPersistence> skillGoalStatuses =
                userGoalStatusRepository.findByUserAndGoal_Category(user, GoalCategory.SKILL);

        Stream<UserGoalStatusPersistence> skillGoalStatusStream = skillGoalStatuses.stream();

        Stream<UserGoalStatusDTO> skillGoalStatusDTOStream =
                skillGoalStatusStream.map(UserGoalStatusDataMapper::toDTO);

        List<UserGoalStatusDTO> skillGoalStatusDTOs =
                skillGoalStatusDTOStream.collect(Collectors.toList());

        return skillGoalStatusDTOs;
    }

    public List<GoalDTO> getGoalDefinitions(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return goalRepository.findTwoRandomForUser(user.getId())
                .stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Silently reviews all in-progress APP_USAGE goals from previous days.
     * Compares the stored daily metrics against each goal's target_override and
     * marks the goal COMPLETED (with points) or NOT_COMPLETED accordingly.
     * No user interaction is required.
     */
    @Transactional
    public List<UserGoalStatusDTO> autoReviewAppUsageGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate today = LocalDate.now();

        List<UserGoalStatusPersistence> pending =
                userGoalStatusRepository.findByUserAndStatusAndDateBeforeAndGoal_CategoryOrderByDateDesc(
                        user, GoalStatus.IN_PROGRESS, today, GoalCategory.APP_USAGE);

        List<UserGoalStatusDTO> results = new ArrayList<>();

        for (UserGoalStatusPersistence entry : pending) {
            Optional<MetricsPersistence> metricsOpt =
                    metricsRepository.findByUserAndDate(user, entry.getDate());

            if (metricsOpt.isEmpty()) {
                // No metrics stored for that day — skip, cannot evaluate
                continue;
            }

            MetricsPersistence metrics = metricsOpt.get();
            Integer targetOverride = entry.getTargetOverride();
            if (targetOverride == null || targetOverride <= 0) {
                continue;
            }

            long actualUsageMs;
            boolean achieved;

            if (entry.getGoal().getType() == GoalType.APP_USAGE_GOOD) {
                actualUsageMs = (long) metrics.getCurrentGoodTimeSeconds() * 1000L;
                achieved = actualUsageMs >= targetOverride;
            } else if (entry.getGoal().getType() == GoalType.APP_USAGE_BAD) {
                actualUsageMs = (long) metrics.getCurrentBadTimeSeconds() * 1000L;
                achieved = actualUsageMs <= targetOverride;
            } else {
                continue;
            }

            if (achieved) {
                usersService.addPointsToUser(user.getId(), entry.getGoal().getReward());
                updateUserGoalProgressAfterCompletion(user, entry.getGoal());
                entry.setStatus(GoalStatus.COMPLETED);
                entry.setValue((int) (actualUsageMs / 1000));
            } else {
                updateUserGoalProgressAfterFailure(user, entry.getGoal());
                entry.setStatus(GoalStatus.NOT_COMPLETED);
            }

            results.add(UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(entry)));
        }

        return results;
    }
    // endregion
}
