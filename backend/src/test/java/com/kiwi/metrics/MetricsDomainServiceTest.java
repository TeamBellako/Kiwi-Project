package com.kiwi.metrics;

import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.metrics.controllers.MetricsService;
import com.kiwi.features.metrics.data.MetricsDomain;
import com.kiwi.features.metrics.data.MetricsDTO;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.metrics.exceptions.MetricsConflictException;
import com.kiwi.features.metrics.exceptions.MetricsInvalidException;
import com.kiwi.features.metrics.exceptions.MetricsNotFoundException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.users.UsersRepositoryInMemory;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.common.types.Email;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;

public class MetricsDomainServiceTest {
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
    private final Email validEmail = new Email(validUserDTO().getEmail());
    
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
        assertEquals(MetricsDataMapper.toDomain(metricsDTO).getDate(), savedMetricsPersistence.get().getDate());
        assertEquals(metricsDTO.getCurrentGoodTimeSeconds(), savedMetricsPersistence.get().getCurrentGoodTimeSeconds());
        assertEquals(metricsDTO.getCurrentBadTimeSeconds(), savedMetricsPersistence.get().getCurrentBadTimeSeconds());
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
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsDataMapper.toPersistence(validUsersPersistence, metricsDomain));

        metricsDTO.setMaxGoodTimeSeconds(0);
        metricsDTO.setCurrentGoodTimeSeconds(metricsDTO.getCurrentGoodTimeSeconds() + 1);
        metricsDTO.setMaxBadTimeSeconds(0);
        metricsDTO.setCurrentBadTimeSeconds(metricsDTO.getCurrentBadTimeSeconds() + 1);
        metricsService.updateMetric(validEmail, metricsDTO);
        
        Optional<MetricsPersistence> retrievedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metricsDomain.getDate());
        assert(retrievedMetricsPersistence.isPresent());
        
        assertNotEquals(metricsDomain, MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(metricsDTO.getDate(), MetricsDataMapper.toDTO(retrievedMetricsPersistence.get()).getDate());
        assertEquals(metricsDTO.getCurrentGoodTimeSeconds(), retrievedMetricsPersistence.get().getCurrentGoodTimeSeconds());
        assertEquals(metricsDTO.getCurrentBadTimeSeconds(), retrievedMetricsPersistence.get().getCurrentBadTimeSeconds());
    }

    @Test(expected = MetricsInvalidException.class)
    public void updateInvalidMetrics() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsDataMapper.toPersistence(validUsersPersistence, metricsDomain));

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
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(metricsDTO);
        metricsRepositoryInMemory.saveAndFlush(MetricsDataMapper.toPersistence(validUsersPersistence, metricsDomain));
        
        Optional<MetricsPersistence> savedMetricsPersistence =
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metricsDomain.getDate());
        assert(savedMetricsPersistence.isPresent());
        
        MetricsDTO retrievedMetricsDTO = metricsService.getMetrics(new Email(validUsersPersistence.getEmail()), metricsDomain.getDate());
        assertEquals(metricsDomain, MetricsDataMapper.toDomain(retrievedMetricsDTO));
    }

    @Test
    public void getNonExistingMetric() {
        boolean notFoundException = false;
        try {
            metricsService.getMetrics(new Email(validUsersPersistence.getEmail()), LocalDate.now());
        } catch (MetricsNotFoundException e) {
            notFoundException = true;
        }
        assertTrue(notFoundException);
    }
}
