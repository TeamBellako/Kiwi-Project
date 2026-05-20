package com.kiwi.features.metrics.controllers;

import com.kiwi.features.goals.controllers.UserGoalStatusRepository;
import com.kiwi.features.goals.data.GoalType;
import com.kiwi.features.goals.data.UserGoalStatusPersistence;
import com.kiwi.features.metrics.data.MetricsDomain;
import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.metrics.exceptions.MetricsConflictException;
import com.kiwi.features.metrics.exceptions.MetricsInvalidException;
import com.kiwi.features.metrics.exceptions.MetricsNotFoundException;
import com.kiwi.features.metrics.data.MetricsDTO;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.common.types.Email;
import com.kiwi.common.types.PositiveOrZeroInteger;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

// Default targets used when the user has no AppUsage goal entries yet
import static com.kiwi.features.goals.data.AppUsageGoalTargetCalculator.GOOD_MIN_MS;
import static com.kiwi.features.goals.data.AppUsageGoalTargetCalculator.BAD_MAX_MS;

@Service
public class MetricsService {
    private final MetricsRepository metricsRepository;
    private final UsersService usersService;
    private final UserGoalStatusRepository userGoalStatusRepository;

    @Autowired
    public MetricsService(
            MetricsRepository metricsRepository,
            UsersService usersService,
            UserGoalStatusRepository userGoalStatusRepository) {
        this.metricsRepository = metricsRepository;
        this.usersService = usersService;
        this.userGoalStatusRepository = userGoalStatusRepository;
    }

    @Transactional
    public MetricsDTO createMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        MetricsDomain metricsDomain;
        try {
            metricsDomain = MetricsDataMapper.toDomain(metricsDTO);
        } catch (Exception e) {
            throw new MetricsInvalidException(e.getMessage());
        }

        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        if (metricsRepository.findByUserAndDate(targetUserPersistence, metricsDomain.getDate()).isPresent()) {
            throw new MetricsConflictException(email, metricsDomain.getDate());
        }

        fillMetricInternalValues(metricsDomain, targetUserPersistence);

        MetricsPersistence savedMetrics = metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(targetUserPersistence, metricsDomain));
        return MetricsDataMapper.toDTO(savedMetrics);
    }

    @Transactional
    public MetricsDTO updateMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        UsersPersistence userPersistence = getTargetUserPersistence(email);
        LocalDate date;
        try {
            date = MetricsDataMapper.toDomain(metricsDTO).getDate();
        } catch (IllegalArgumentException e) {
            throw new MetricsInvalidException(e.getMessage());
        }

        Optional<MetricsPersistence> metricsPersistence = metricsRepository.findByUserAndDate(userPersistence, date);
        if (metricsPersistence.isEmpty()) {
            throw new MetricsNotFoundException(email, date);
        }

        MetricsDomain updateMetricsDomain = MetricsDataMapper.toDomain(metricsPersistence.get());
        MetricsDataMapper.updateDomain(updateMetricsDomain, metricsDTO);
        MetricsPersistence updateMetricsPersistence = metricsPersistence.get();
        MetricsDataMapper.updatePersistence(updateMetricsPersistence, updateMetricsDomain);

        MetricsPersistence savedMetrics = metricsRepository.saveAndFlush(updateMetricsPersistence);
        return MetricsDataMapper.toDTO(savedMetrics);
    }

    public MetricsDTO getMetrics(@Valid @NotNull Email email, @NotNull LocalDate date) {
        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        Optional<MetricsPersistence> metricsPersistence = metricsRepository.findByUserAndDate(targetUserPersistence, date);
        if (metricsPersistence.isPresent()) {
            return MetricsDataMapper.toDTO(metricsPersistence.get());
        }
        throw new MetricsNotFoundException(email, date);
    }

    private UsersPersistence getTargetUserPersistence(Email email) {
        Optional<UsersPersistence> targetUserPersistence = usersService.getUserByEmail(email);
        if (targetUserPersistence.isEmpty()) throw new UsersNotFoundException(email.value());
        return targetUserPersistence.get();
    }

    private void fillMetricInternalValues(MetricsDomain metricsDomain, UsersPersistence user) {
        long goodTargetMs = resolveAppUsageTargetMs(user, GoalType.APP_USAGE_GOOD, GOOD_MIN_MS);
        long badTargetMs  = resolveAppUsageTargetMs(user, GoalType.APP_USAGE_BAD,  BAD_MAX_MS);

        int goodTargetSeconds = (int) (goodTargetMs / 1_000);
        int badTargetSeconds  = (int) (badTargetMs  / 1_000);

        metricsDomain.setMaxGoodTimeSeconds(new PositiveOrZeroInteger(goodTargetSeconds));
        metricsDomain.setMaxBadTimeSeconds(new PositiveOrZeroInteger(badTargetSeconds));
    }

    /**
     * Returns the most recent target_override (ms) for the given AppUsage goal type.
     * Falls back to {@code defaultMs} if no entry exists yet.
     */
    private long resolveAppUsageTargetMs(UsersPersistence user, GoalType type, long defaultMs) {
        Optional<UserGoalStatusPersistence> latest =
                userGoalStatusRepository.findFirstByUserAndGoal_TypeOrderByDateDesc(user, type);
        if (latest.isPresent() && latest.get().getTargetOverride() != null) {
            return latest.get().getTargetOverride().longValue();
        }
        return defaultMs;
    }
}
