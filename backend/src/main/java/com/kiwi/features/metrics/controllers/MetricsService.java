package com.kiwi.features.metrics.controllers;

import com.kiwi.features.metrics.data.MetricsDomain;
import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.metrics.exceptions.MetricsConflictException;
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

@Service
public class MetricsService {
    private final MetricsRepository metricsRepository;
    private final UsersService usersService;
    
    @Autowired
    public MetricsService(MetricsRepository metricsRepository, UsersService usersService) {
        this.metricsRepository = metricsRepository;
        this.usersService = usersService;
    }

    @Transactional
    public MetricsDTO createMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(metricsDTO);
        fillMetricInternalValues(metricsDomain);

        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        if (metricsRepository.findByUserAndDate(targetUserPersistence, metricsDomain.getDate()).isPresent()) {
            throw new MetricsConflictException(email, metricsDomain.getDate());
        }

        MetricsPersistence savedMetrics = metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(targetUserPersistence, metricsDomain));
        return MetricsDataMapper.toDTO(savedMetrics);
    }
    
    @Transactional
    public MetricsDTO updateMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        UsersPersistence userPersistence = getTargetUserPersistence(email);
        LocalDate date = MetricsDataMapper.toDomain(metricsDTO).getDate();
        Optional<MetricsPersistence> metricsPersistence = metricsRepository.findByUserAndDate(userPersistence, date);
        if (metricsPersistence.isEmpty()) {
            throw new MetricsNotFoundException(email, date);
        }

        MetricsDomain updateMetricsDomain = MetricsDataMapper.toDomain(metricsPersistence.get());
        updateMetricsDomain.update(metricsDTO);
        MetricsPersistence updateMetricsPersistence = MetricsDataMapper.toPersistence(userPersistence, updateMetricsDomain);

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

    private void fillMetricInternalValues(MetricsDomain metricsDomain) {
        // TODO calculate with formula depending on ? (personality, previous metrics, etc.)
        metricsDomain.setMaxGoodTimeSeconds(new PositiveOrZeroInteger(Math.round(0.5f * 3600)));
        metricsDomain.setMaxBadTimeSeconds(new PositiveOrZeroInteger(10 * 3600));
    }
}
