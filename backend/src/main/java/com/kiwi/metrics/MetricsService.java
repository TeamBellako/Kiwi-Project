package com.kiwi.metrics;

import com.kiwi.users.*;
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
    public void createMetric(@Valid @NotNull MetricsDTO metricsDTO) {
        Metrics metrics;
        try {
            metrics = MetricsMapper.toDomain(metricsDTO);
        } catch (IllegalArgumentException e) {
            throw new MetricsInvalidException(e.getMessage());
        }
        
        Optional<UsersPersistence> targetUserPersistence = usersService.getUserByEmail(metrics.getEmail());
        if (targetUserPersistence.isEmpty()) throw new UsersNotFoundException(metrics.getEmail().value());
        
        if (metricsRepository.findByUserAndDate(targetUserPersistence.get(), metrics.getDate()).isPresent()) {
            throw new MetricsConflictException(metrics.getEmail(), metrics.getDate());
        }

        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(targetUserPersistence.get(), metrics));
    }
    
    @Transactional
    public void updateMetric(@Valid @NotNull MetricsDTO metricsDTO) {
        // TODO
    }
    
    public Optional<MetricsDTO> getMetricsByUserAndDate(@Valid @NotNull Email email, @NotNull LocalDate date) {
        // TODO        
        return Optional.empty();
    }
}
