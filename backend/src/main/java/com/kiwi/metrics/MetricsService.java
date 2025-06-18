package com.kiwi.metrics;

import com.kiwi.users.Email;
import com.kiwi.users.Users;
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
    
    @Autowired
    public MetricsService(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }
    
    @Transactional
    public void createMetric(@Valid @NotNull MetricsDTO metricsDTO) {
//        Metrics metrics;
//        try {
//            metrics = MetricsMapper.toDomain(metricsDTO);
//        } catch (IllegalArgumentException e) {
//            throw new MetricsInvalidException(e.getMessage());
//        }
//        
////        if (metricsRepository.findByUserAndDate(new Users(metrics.getEmail()), metrics.getDate())) {
////            throw new MetricsConflictException(metrics.getEmail(), metrics.getDate());
////        }
//        
//        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(metrics));
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
