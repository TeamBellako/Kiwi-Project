package com.kiwi.metrics;

import com.kiwi.settings.SettingsRepository;
import com.kiwi.users.UsersRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/DBTestSetUp.sql")
@ActiveProfiles("test")
public class MetricsRepositoryTest {
    @Autowired
    private MetricsRepository metricsRepository;

    @Test
    public void createValidMetrics() {
        Metrics metrics = MetricsMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(metrics));
        
        Optional<MetricsPersistence> savedMetricsPersistence = metricsRepository.findByDate(metrics.getDate());
        assert(savedMetricsPersistence.isPresent());
        
        assertEquals(metrics, MetricsMapper.toDomain(savedMetricsPersistence.get()));
    }

    @Test
    public void updateValidMetrics() {
        Metrics metrics = MetricsMapper.toDomain(MetricsFactory.generateRandomValidMetricDTO());
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(metrics));

        Optional<MetricsPersistence> savedMetricsPersistence = metricsRepository.findByDate(metrics.getDate());
        assert(savedMetricsPersistence.isPresent());
        savedMetricsPersistence.get().setSteps(new PositiveOrZeroInteger(metrics.getSteps().value() + 1));
        metricsRepository.saveAndFlush(savedMetricsPersistence.get());

        Optional<MetricsPersistence> savedUpdatedMetricsPersistence = metricsRepository.findByDate(savedMetricsPersistence.get().getDate());
        assert(savedUpdatedMetricsPersistence.isPresent());
        assertNotEquals(metrics, MetricsMapper.toDomain(savedUpdatedMetricsPersistence.get()));
    }

    @Test
    public void getNonExistingMetrics() {
        assertEquals(Optional.empty(), metricsRepository.findByDate(MetricsFactory.generateRandomValidMetricDTO().getDate()));
    }
}
