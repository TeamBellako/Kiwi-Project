package com.kiwi.metrics;

import com.kiwi.users.UsersMapper;
import com.kiwi.users.UsersPersistence;
import org.junit.Test;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricsServiceTest {
    private final MetricsRepositoryInMemory metricsRepositoryInMemory = new MetricsRepositoryInMemory();
    private final MetricsService metricsService = new MetricsService(metricsRepositoryInMemory);
    
    private final UsersPersistence validUsersPersitence = UsersMapper.toPersistence(UsersMapper.toDomain(validUserDTO()), "");
    
    @Test
    public void createValidMetric() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        metricsService.createMetric(metricsDTO);
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersitence, metricsDTO.getDate());
        assert(savedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(metricsDTO), MetricsMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test(expected = MetricsInvalidException.class)
    public void createInvalidMetric() {
        MetricsDTO invalidMetricsDTO = MetricsFactory.generateRandomInvalidMetricDTO();
        metricsService.createMetric(invalidMetricsDTO);
    }

    @Test(expected = NullPointerException.class)
    public void createNullMetric() {
        metricsService.createMetric(null);
    }

    @Test(expected = MetricsConflictException.class)
    public void createDuplicatedMetric() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        
        metricsService.createMetric(metricsDTO);
        metricsService.createMetric(metricsDTO);
    }

    @Test
    public void updateValidMetric() {
        
    }

    @Test
    public void updateInvalidMetric() {

    }

    @Test
    public void updateNullMetric() {

    }

    @Test
    public void updateNonExistingMetric() {

    }

    @Test
    public void getExistingMetric() {

    }

    @Test
    public void getNonExistingMetric() {

    }
}
