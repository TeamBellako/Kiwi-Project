package com.kiwi.metrics;

import com.kiwi.users.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricsServiceTest {
    private final UsersRepositoryInMemory usersRepositoryInMemory = new UsersRepositoryInMemory();
    private final UsersService usersService = new UsersService(usersRepositoryInMemory, new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return true;
        }
    });
    
    private final MetricsRepositoryInMemory metricsRepositoryInMemory = new MetricsRepositoryInMemory();
    private final MetricsService metricsService = new MetricsService(metricsRepositoryInMemory, usersService);
    
    private UsersPersistence validUsersPersistence;
    
    @Before
    public void setUp() {
        usersService.createUser(validUserDTO());
        validUsersPersistence = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).get();
    }
    
    @Test
    public void createValidMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        metricsService.createMetric(metricsDTO);
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metricsDTO.getDate());
        assert(savedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(metricsDTO), MetricsMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test(expected = MetricsInvalidException.class)
    public void createInvalidMetrics() {
        MetricsDTO invalidMetricsDTO = MetricsFactory.generateRandomInvalidMetricDTO();
        metricsService.createMetric(invalidMetricsDTO);
    }

    @Test(expected = NullPointerException.class)
    public void createNullMetrics() {
        metricsService.createMetric(null);
    }

    @Test(expected = MetricsConflictException.class)
    public void createDuplicatedMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        
        metricsService.createMetric(metricsDTO);
        metricsService.createMetric(metricsDTO);
    }

    @Test
    public void updateValidMetrics() {
        
    }

    @Test
    public void updateInvalidMetrics() {

    }

    @Test
    public void updateNullMetrics() {

    }

    @Test
    public void updateNonExistingMetrics() {

    }
    
    @Test
    public void getExistingMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        Metrics metrics = MetricsMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsMapper.toPersistence(validUsersPersistence, metrics));
        
        Optional<MetricsPersistence> savedMetricsPersistence =
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metrics.getDate());
        assert(savedMetricsPersistence.isPresent());
        
        Optional<MetricsDTO> retrievedMetricsDTO =
                metricsService.getMetricsByEmailAndDate(validUsersPersistence.getEmail(), metrics.getDate());
        assert(retrievedMetricsDTO.isPresent());
        
        assertEquals(metrics, MetricsMapper.toDomain(retrievedMetricsDTO.get()));
    }

    @Test
    public void getNonExistingMetric() {
        assertEquals(Optional.empty(), metricsService.getMetricsByEmailAndDate(validUsersPersistence.getEmail(), LocalDate.now()));
    }
}
