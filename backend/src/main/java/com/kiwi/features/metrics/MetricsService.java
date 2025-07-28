package com.kiwi.features.metrics;

import com.kiwi.features.users.UsersNotFoundException;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersService;
import com.kiwi.types.Email;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public void createMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        Metrics metrics = MetricsMapper.toDomain(metricsDTO);

        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        if (metricsRepository.findByUserAndDate(targetUserPersistence, metrics.getDate()).isPresent()) {
            throw new MetricsConflictException(email, metrics.getDate());
        }

        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(targetUserPersistence, metrics));
    }
    
    @Transactional
    public void updateMetric(@Valid @NotNull Email email, @Valid @NotNull MetricsDTO metricsDTO) {
        Metrics updateMetrics = MetricsMapper.toDomain(metricsDTO);
        
        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        Optional<MetricsPersistence> targetMetricsPersistence = metricsRepository.findByUserAndDate(targetUserPersistence, updateMetrics.getDate());
        if (targetMetricsPersistence.isEmpty()) {
            throw new MetricsNotFoundException(email, updateMetrics.getDate());
        }

        targetMetricsPersistence.get().mergeFromDomain(updateMetrics);
        metricsRepository.saveAndFlush(targetMetricsPersistence.get());
    }
    
    public Optional<MetricsDTO> getMetrics(@Valid @NotNull Email email, @NotNull LocalDate date) {
        UsersPersistence targetUserPersistence = getTargetUserPersistence(email);
        
        Optional<MetricsPersistence> metricsPersistence = metricsRepository.findByUserAndDate(targetUserPersistence, date);
        if (metricsPersistence.isEmpty()) {
            return Optional.empty();
        } 
        
        Metrics metrics = MetricsMapper.toDomain(metricsPersistence.get());
        return Optional.of(MetricsMapper.toDTO(metrics));
    }
    
    private UsersPersistence getTargetUserPersistence(Email email) {
        Optional<UsersPersistence> targetUserPersistence = usersService.getUserByEmail(email);
        if (targetUserPersistence.isEmpty()) throw new UsersNotFoundException(email.value());
        
        return targetUserPersistence.get();
    }
}
