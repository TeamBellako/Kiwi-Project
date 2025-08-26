package com.kiwi.metrics;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.metrics.tests.MetricsFactory;
import com.kiwi.features.metrics.data.MetricsDataMapper;
import com.kiwi.features.metrics.controllers.MetricsRepository;
import com.kiwi.features.metrics.controllers.MetricsService;
import com.kiwi.features.metrics.data.MetricsDTO;
import com.kiwi.features.metrics.data.MetricsPersistence;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/MetricsTestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class})
public class MetricsDomainIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private MetricsRepository metricsRepository;
    @Autowired
    private MetricsService metricsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private final String APIURL = "/api/user/metrics";
    
    private final MetricsDTO validMetricsDTO = MetricsFactory.generateRandomValidMetricDTO();
    private final MetricsDTO invalidMetricsDTO = MetricsFactory.generateRandomInvalidMetricDTO();
    
    @Autowired
    private UsersRepository usersRepository;
    
    private UsersPersistence validUserPersistence;
    
    @Before
    public void setUp() {
        validUserPersistence = usersRepository.findByEmail(validUserDTO().getEmail()).get();
    }
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createValidMetrics() throws Exception {
        mockMvc.perform(post(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMetricsDTO)))
                .andExpect(status().isCreated());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO).getDate(), retrievedMetricsPersistence.get().getDate());
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO).getCurrentGoodTimeSeconds(), retrievedMetricsPersistence.get().getCurrentGoodTimeSeconds());
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO).getCurrentBadTimeSeconds(), retrievedMetricsPersistence.get().getCurrentBadTimeSeconds());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createInvalidMetrics() throws Exception {
        mockMvc.perform(post(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMetricsDTO)))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(invalidMetricsDTO.getDate()));
        
        assert(retrievedMetricsPersistence.isEmpty());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createNullMetrics() throws Exception {
        mockMvc.perform(post(APIURL))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));

        assert(retrievedMetricsPersistence.isEmpty());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createDuplicatedMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, MetricsDataMapper.toDomain(validMetricsDTO)));
        
        MetricsDTO duplicatedUpdatedMetricsDTO = validMetricsDTO.copy();
        duplicatedUpdatedMetricsDTO.setMaxGoodTimeSeconds(validMetricsDTO.getMaxGoodTimeSeconds() + 1);
        duplicatedUpdatedMetricsDTO.setCurrentGoodTimeSeconds(validMetricsDTO.getCurrentGoodTimeSeconds() + 1);
        duplicatedUpdatedMetricsDTO.setMaxBadTimeSeconds(validMetricsDTO.getMaxBadTimeSeconds() + 1);
        duplicatedUpdatedMetricsDTO.setCurrentBadTimeSeconds(validMetricsDTO.getCurrentBadTimeSeconds() + 1);
        
        mockMvc.perform(post(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedUpdatedMetricsDTO)))
                .andExpect(status().isConflict());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertNotEquals(MetricsDataMapper.toDomain(duplicatedUpdatedMetricsDTO), MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO), MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateValidMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, MetricsDataMapper.toDomain(validMetricsDTO)));
        MetricsDTO updatedMetricsDTO = validMetricsDTO.copy();
        updatedMetricsDTO.setCurrentGoodTimeSeconds(validMetricsDTO.getCurrentGoodTimeSeconds() + 1);
        updatedMetricsDTO.setCurrentBadTimeSeconds(validMetricsDTO.getCurrentBadTimeSeconds() + 1);

        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMetricsDTO)))
                .andExpect(status().isOk());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertNotEquals(MetricsDataMapper.toDomain(validMetricsDTO), MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(MetricsDataMapper.toDomain(updatedMetricsDTO).getDate(), retrievedMetricsPersistence.get().getDate());
        assertEquals(MetricsDataMapper.toDomain(updatedMetricsDTO).getCurrentGoodTimeSeconds(), retrievedMetricsPersistence.get().getCurrentGoodTimeSeconds());
        assertEquals(MetricsDataMapper.toDomain(updatedMetricsDTO).getCurrentBadTimeSeconds(), retrievedMetricsPersistence.get().getCurrentBadTimeSeconds());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateInvalidMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, MetricsDataMapper.toDomain(validMetricsDTO)));
        MetricsDTO updatedMetricsDTO = invalidMetricsDTO.copy();
        Integer invalidTime = 25 * 60 * 60;
        updatedMetricsDTO.setCurrentGoodTimeSeconds(validMetricsDTO.getCurrentGoodTimeSeconds() - invalidTime);
        updatedMetricsDTO.setCurrentBadTimeSeconds(validMetricsDTO.getCurrentBadTimeSeconds() - invalidTime);
        
        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMetricsDTO)))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO), MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateNullMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, MetricsDataMapper.toDomain(validMetricsDTO)));

        mockMvc.perform(put(APIURL))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsDataMapper.toDomain(validMetricsDTO), MetricsDataMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateNonExistingMetrics() throws Exception {
        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMetricsDTO)))
                .andExpect(status().isNotFound());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isEmpty());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getExistingMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsDataMapper.toPersistence(validUserPersistence, MetricsDataMapper.toDomain(validMetricsDTO)));

        mockMvc.perform(get(APIURL)
                        .param("date", validMetricsDTO.getDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxGoodTimeSeconds").value(validMetricsDTO.getMaxGoodTimeSeconds()))
                .andExpect(jsonPath("$.currentGoodTimeSeconds").value(validMetricsDTO.getCurrentGoodTimeSeconds()))
                .andExpect(jsonPath("$.maxBadTimeSeconds").value(validMetricsDTO.getMaxBadTimeSeconds()))
                .andExpect(jsonPath("$.currentBadTimeSeconds").value(validMetricsDTO.getCurrentBadTimeSeconds()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getNonExistingMetrics() throws Exception {
        mockMvc.perform(get(APIURL)
                        .param("date", validMetricsDTO.getDate()))
                .andExpect(status().isNotFound());
    }
}
