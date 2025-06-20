package com.kiwi.metrics;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.GlobalExceptionHandler;
import com.kiwi.common.JacksonConfig;
import com.kiwi.security.JwtUtils;
import com.kiwi.security.WebSecurityConfig;
import com.kiwi.users.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

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
    public void createInvalidMetrics() throws Exception {

    }

    @Test
    public void createNullMetrics() throws Exception {

    }

    @Test
    public void createDuplicatedMetrics() throws Exception {

    }

    @Test
    public void createMetricsWithImpersonatedUser() throws Exception {

    }

    @Test
    public void updateValidMetrics() throws Exception {

    }

    @Test
    public void updateInvalidMetrics() throws Exception {

    }

    @Test
    public void updateNullMetrics() throws Exception {

    }

    @Test
    public void updateNonExistingMetrics() throws Exception {

    }

    @Test
    public void updateMetricsWithImpersonatedUser() throws Exception {

    }

    @Test
    public void getExistingMetrics() throws Exception {

    }

    @Test
    public void getNonExistingMetrics() throws Exception {

    }

    @Test
    public void getMetricsWithImpersonatedUser() throws Exception {

    }
}
