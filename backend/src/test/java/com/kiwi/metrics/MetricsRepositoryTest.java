package com.kiwi.metrics;

import com.kiwi.users.UsersPersistence;
import com.kiwi.users.UsersRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/MetricsTestSetUp.sql")
@ActiveProfiles("test")
public class MetricsRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MetricsRepository metricsRepository;
    
    private UsersPersistence validUserPersistence;
    
    @Before
    public void setUp() {
        validUserPersistence = usersRepository.findByEmail(validUserDTO().getEmail()).get();
    }
    
    @Test
    public void createValidMetrics() {
        Metrics metrics = MetricsMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, metrics));
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepository.findByUserAndDate(validUserPersistence, metrics.getDate());
        assert(savedMetricsPersistence.isPresent());
        
        assertEquals(metrics, MetricsMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test
    public void updateValidMetrics() {
        Metrics metrics = MetricsMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, metrics));

        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepository.findByUserAndDate(validUserPersistence, metrics.getDate());
        assert(savedMetricsPersistence.isPresent());
        savedMetricsPersistence.get().setSteps(new PositiveOrZeroInteger(metrics.getSteps().value() + 1));
        metricsRepository.saveAndFlush(savedMetricsPersistence.get());

        Optional<MetricsPersistence> savedUpdatedMetricsPersistence = 
                metricsRepository.findByUserAndDate(validUserPersistence, savedMetricsPersistence.get().getDate());
        assert(savedUpdatedMetricsPersistence.isPresent());
        assertNotEquals(metrics, MetricsMapper.toDomain(savedUpdatedMetricsPersistence.get()));
    }

    @Test
    public void getNonExistingMetrics() {
        assertEquals(
                Optional.empty(),
                metricsRepository.findByUserAndDate(validUserPersistence, MetricsFactory.generateRandomValidMetricDTO().getDate())
        );
    }
}