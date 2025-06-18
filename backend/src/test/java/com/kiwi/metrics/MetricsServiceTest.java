package com.kiwi.metrics;

import com.kiwi.users.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public void createValidMetric() {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        metricsService.createMetric(metricsDTO);
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepositoryInMemory.findByUserAndDate(validUsersPersistence, metricsDTO.getDate());
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
