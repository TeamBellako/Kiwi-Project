package com.kiwi.metrics;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.metrics.*;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersRepository;
import com.kiwi.utils.GlobalExceptionHandler;
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
public class MetricsIntegrationTest {
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
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
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
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));
        
        MetricsDTO duplicatedUpdatedMetricsDTO = validMetricsDTO.copy();
        duplicatedUpdatedMetricsDTO.setSteps(validMetricsDTO.getSteps() + 1);
        
        mockMvc.perform(post(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedUpdatedMetricsDTO)))
                .andExpect(status().isConflict());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertNotEquals(MetricsMapper.toDomain(duplicatedUpdatedMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "jake@thedog.com")
    public void createMetricsWithImpersonatedUser() throws Exception {
        mockMvc.perform(post(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMetricsDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateValidMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));
        MetricsDTO updatedMetricsDTO = validMetricsDTO.copy();
        updatedMetricsDTO.setSteps(validMetricsDTO.getSteps() + 1);

        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMetricsDTO)))
                .andExpect(status().isOk());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertNotEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
        assertEquals(MetricsMapper.toDomain(updatedMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateInvalidMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));
        MetricsDTO updatedMetricsDTO = invalidMetricsDTO.copy();
        updatedMetricsDTO.setSteps(validMetricsDTO.getSteps() - 1);
        
        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMetricsDTO)))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateNullMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));

        mockMvc.perform(put(APIURL))
                .andExpect(status().isBadRequest());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
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
    @WithMockUser(username = "jake@thedog.com")
    public void updateMetricsWithImpersonatedUser() throws Exception {
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));

        MetricsDTO updatedMetricsDTO = validMetricsDTO.copy();
        updatedMetricsDTO.setSteps(validMetricsDTO.getSteps() + 1);

        mockMvc.perform(put(APIURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMetricsDTO)))
                .andExpect(status().isUnauthorized());

        Optional<MetricsPersistence> retrievedMetricsPersistence =
                metricsRepository.findByUserAndDate(validUserPersistence, LocalDate.parse(validMetricsDTO.getDate()));
        assert(retrievedMetricsPersistence.isPresent());
        assertEquals(MetricsMapper.toDomain(validMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
        assertNotEquals(MetricsMapper.toDomain(updatedMetricsDTO), MetricsMapper.toDomain(retrievedMetricsPersistence.get()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getExistingMetrics() throws Exception {
        metricsRepository.saveAndFlush(MetricsMapper.toPersistence(validUserPersistence, MetricsMapper.toDomain(validMetricsDTO)));

        mockMvc.perform(get(APIURL)
                        .param("email", validMetricsDTO.getEmail())
                        .param("date", validMetricsDTO.getDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps").value(validMetricsDTO.getSteps()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getNonExistingMetrics() throws Exception {
        mockMvc.perform(get(APIURL)
                        .param("email", validMetricsDTO.getEmail())
                        .param("date", validMetricsDTO.getDate()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "jake@thedog.com")
    public void getMetricsWithImpersonatedUser() throws Exception {
        mockMvc.perform(get(APIURL)
                        .param("email", validMetricsDTO.getEmail())
                        .param("date", validMetricsDTO.getDate()))
                .andExpect(status().isUnauthorized());
    }
}
