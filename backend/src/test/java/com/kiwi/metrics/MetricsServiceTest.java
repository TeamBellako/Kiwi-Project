package com.kiwi.metrics;

import com.kiwi.features.metrics.*;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersRepositoryInMemory;
import com.kiwi.features.users.UsersService;
import com.kiwi.types.Email;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    private final Email validEmail = new Email("finn@thehuman.com");
    
    @Before
    public void setUp() {
        usersService.createUser(validUserDTO());
        validUsersPersistence = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).get();
    }
    
    @Test
    public void createValidMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        metricsService.createMetric(validEmail, metricsDTO);
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, LocalDate.parse(metricsDTO.getDate()));
        assert(savedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(metricsDTO), MetricsMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test(expected = MetricsInvalidException.class)
    public void createInvalidMetrics() {
        MetricsDTO invalidMetricsDTO = MetricsFactory.generateRandomInvalidMetricDTO();
        metricsService.createMetric(validEmail, invalidMetricsDTO);
    }

    @Test(expected = NullPointerException.class)
    public void createNullMetrics() {
        metricsService.createMetric(validEmail, null);
    }

    @Test(expected = MetricsConflictException.class)
    public void createDuplicatedMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        
        metricsService.createMetric(validEmail, metricsDTO);
        metricsService.createMetric(validEmail, metricsDTO);
    }

    @Test
    public void updateValidMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        Metrics metrics = MetricsMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsMapper.toPersistence(validUsersPersistence, metrics));

        metricsDTO.setMaxGoodTimeSeconds(metricsDTO.getMaxGoodTimeSeconds() + 1);
        metricsDTO.setCurrentGoodTimeSeconds(metricsDTO.getCurrentGoodTimeSeconds() + 1);
        metricsDTO.setMaxBadTimeSeconds(metricsDTO.getMaxBadTimeSeconds() + 1);
        metricsDTO.setCurrentBadTimeSeconds(metricsDTO.getCurrentBadTimeSeconds() + 1);
        metricsService.updateMetric(validEmail, metricsDTO);
        
        Optional<MetricsPersistence> retrievedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metrics.getDate());
        assert(retrievedMetricsPersistence.isPresent());
        
        assertNotEquals(metrics, MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(MetricsMapper.toDomain(metricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test(expected = MetricsInvalidException.class)
    public void updateInvalidMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        Metrics metrics = MetricsMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsMapper.toPersistence(validUsersPersistence, metrics));

        metricsService.updateMetric(validEmail, MetricsFactory.generateRandomInvalidMetricDTO());
    }

    @Test(expected = NullPointerException.class)
    public void updateNullMetrics() {
        metricsService.updateMetric(validEmail, null);
    }

    @Test(expected = MetricsNotFoundException.class)
    public void updateNonExistingMetrics() {
        metricsService.updateMetric(validEmail, MetricsFactory.generateRandomValidMetricDTO());
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
                metricsService.getMetrics(validUsersPersistence.getEmail(), metrics.getDate());
        assert(retrievedMetricsDTO.isPresent());
        
        assertEquals(metrics, MetricsMapper.toDomain(retrievedMetricsDTO.get()));
    }

    @Test
    public void getNonExistingMetric() {
        assertEquals(Optional.empty(), metricsService.getMetrics(validUsersPersistence.getEmail(), LocalDate.now()));
    }
}
