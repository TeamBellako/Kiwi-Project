package com.kiwi.features.users.controllers;

import com.kiwi.features.goals.controllers.UserGoalProgressRepository;
import com.kiwi.features.goals.data.GoalType;
import com.kiwi.features.goals.data.UserGoalProgressKey;
import com.kiwi.features.goals.data.UserGoalProgressPersistence;
import com.kiwi.features.users.data.AppUsageType;
import com.kiwi.features.users.data.UserAppUsageDTO;
import com.kiwi.features.users.data.UserAppUsagePersistence;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import com.kiwi.common.types.Email;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAppUsageService {
    private final UserAppUsageRepository repository;
    private final UsersService usersService;
    private final UserGoalProgressRepository goalProgressRepository;

    @Autowired
    public UserAppUsageService(UserAppUsageRepository repository,
                               UsersService usersService,
                               UserGoalProgressRepository goalProgressRepository) {
        this.repository = repository;
        this.usersService = usersService;
        this.goalProgressRepository = goalProgressRepository;
    }

    @Transactional
    public UserAppUsageDTO saveBaselineIfNotExists(Email email, UserAppUsageDTO dto) {
        Optional<UsersPersistence> userOpt = usersService.getUserByEmail(email);
        if (userOpt.isEmpty()) throw new UsersNotFoundException(email.value());
        UsersPersistence user = userOpt.get();

        if (!repository.existsByUserAndAppType(user, AppUsageType.GOOD)) {
            repository.save(UserAppUsagePersistence.builder()
                    .user(user)
                    .appType(AppUsageType.GOOD)
                    .avgDailyUsageMs(dto.getAvgGoodDailyUsageMs())
                    .recordedAt(LocalDateTime.now())
                    .build());
        }

        if (!repository.existsByUserAndAppType(user, AppUsageType.BAD)) {
            repository.save(UserAppUsagePersistence.builder()
                    .user(user)
                    .appType(AppUsageType.BAD)
                    .avgDailyUsageMs(dto.getAvgBadDailyUsageMs())
                    .recordedAt(LocalDateTime.now())
                    .build());
        }

        initGoalProgressIfAbsent(user, GoalType.APP_USAGE_GOOD.name());
        initGoalProgressIfAbsent(user, GoalType.APP_USAGE_BAD.name());

        return dto;
    }

    private void initGoalProgressIfAbsent(UsersPersistence user, String goalType) {
        UserGoalProgressKey key = UserGoalProgressKey.builder()
                .userId(user.getId())
                .goalType(goalType)
                .build();

        if (!goalProgressRepository.existsById(key)) {
            goalProgressRepository.save(UserGoalProgressPersistence.builder()
                    .id(key)
                    .currentDifficulty(1)
                    .goalsCompletedAtDifficulty(0)
                    .goalsFailedAtDifficulty(0)
                    .build());
        }
    }
}
