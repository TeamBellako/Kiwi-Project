package com.kiwi.features.metrics;

import com.kiwi.features.users.UsersNotFoundException;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersService;
import com.kiwi.types.Email;
import com.kiwi.types.PositiveOrZeroInteger;
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
        Metrics metrics = MetricsMapper.toDomain(metricsDTO);
        fillMetricInternalValues(metrics);

        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        if (metricsRepository.findByUserAndDate(targetUserPersistence, metrics.getDate()).isPresent()) {
            throw new MetricsConflictException(email, metrics.getDate());
        }

        MetricsPersistence savedMetrics = metricsRepository.saveAndFlush(MetricsMapper.toPersistence(targetUserPersistence, metrics));
        return MetricsMapper.toDTO(MetricsMapper.toDomain(savedMetrics));
    }
    
    @Transactional
    public MetricsDTO updateMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        Metrics updateMetrics = MetricsMapper.toDomain(metricsDTO);
        
        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        Optional<MetricsPersistence> targetMetricsPersistence = metricsRepository.findByUserAndDate(targetUserPersistence, updateMetrics.getDate());
        if (targetMetricsPersistence.isEmpty()) {
            throw new MetricsNotFoundException(email, updateMetrics.getDate());
        }

        targetMetricsPersistence.get().updateFromDomain(updateMetrics);
        MetricsPersistence savedMetrics = metricsRepository.saveAndFlush(targetMetricsPersistence.get());
        return MetricsMapper.toDTO(MetricsMapper.toDomain(savedMetrics));
    }
    
    public MetricsDTO getMetrics(@Valid @NotNull Email email, @NotNull LocalDate date) {
        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        Optional<MetricsPersistence> metricsPersistence = metricsRepository.findByUserAndDate(targetUserPersistence, date);
        if (metricsPersistence.isPresent()) {
            return MetricsMapper.toDTO(MetricsMapper.toDomain(metricsPersistence.get()));
        }
        throw new MetricsNotFoundException(email, date);
    }
    
    private UsersPersistence getTargetUserPersistence(Email email) {
        Optional<UsersPersistence> targetUserPersistence = usersService.getUserByEmail(email);
        if (targetUserPersistence.isEmpty()) throw new UsersNotFoundException(email.value());
        
        return targetUserPersistence.get();
    }

    private void fillMetricInternalValues(Metrics metrics) {
        // TODO calculate with formula depending on ? (personality, previous metrics, etc.)
        metrics.setMaxGoodTimeSeconds(new PositiveOrZeroInteger(Math.round(0.5f * 3600)));
        metrics.setMaxBadTimeSeconds(new PositiveOrZeroInteger(10 * 3600));
    }
}
