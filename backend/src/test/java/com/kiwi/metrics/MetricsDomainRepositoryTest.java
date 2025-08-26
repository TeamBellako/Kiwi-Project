package com.kiwi.metrics;

import com.kiwi.features.metrics.data.MetricsDomain;
import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.metrics.controllers.MetricsRepository;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
public class MetricsDomainRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MetricsRepository metricsRepository;
    
    private UsersPersistence validUserPersistence;
    
    @Before
    public void setUp() {
        validUserPersistence = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);
    }
    
    @Test
    public void createValidMetrics() {
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, metricsDomain));
        
        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepository.findByUserAndDate(validUserPersistence, metricsDomain.getDate());
        assert(savedMetricsPersistence.isPresent());
        
        assertEquals(metricsDomain, MetricsDataMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test
    public void updateValidMetrics() {
        MetricsDomain metricsDomain = MetricsDataMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, metricsDomain));

        Optional<MetricsPersistence> savedMetricsPersistence = 
                metricsRepository.findByUserAndDate(validUserPersistence, metricsDomain.getDate());
        assert(savedMetricsPersistence.isPresent());
        savedMetricsPersistence.get().setMaxGoodTimeSeconds(metricsDomain.getMaxGoodTimeSeconds().value() + 1);
        savedMetricsPersistence.get().setCurrentGoodTimeSeconds(metricsDomain.getCurrentGoodTimeSeconds().value() + 1);
        savedMetricsPersistence.get().setMaxBadTimeSeconds(metricsDomain.getMaxBadTimeSeconds().value() + 1);
        savedMetricsPersistence.get().setCurrentBadTimeSeconds(metricsDomain.getCurrentBadTimeSeconds().value() + 1);
        metricsRepository.saveAndFlush(savedMetricsPersistence.get());

        Optional<MetricsPersistence> savedUpdatedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, savedMetricsPersistence.get().getDate());
        assert(savedUpdatedMetricsPersistence.isPresent());
        assertNotEquals(metricsDomain, MetricsDataMapper.toDomain(savedUpdatedMetricsPersistence.get()));
    }

    @Test
    public void getNonExistingMetrics() {
        assertEquals(
                Optional.empty(),
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(MetricsFactory.generateRandomValidMetricDTO().getDate()))
        );
    }
}