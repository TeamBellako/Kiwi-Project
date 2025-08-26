package com.kiwi.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.metrics.controllers.MetricsController;
import com.kiwi.features.metrics.data.MetricsDTO;
import com.kiwi.features.metrics.controllers.MetricsService;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.common.types.Email;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static com.kiwi.utils.HTTPTestUtils.getPutRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MetricsController.class)
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JacksonConfig.class})
public class MetricsDomainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;
    
    @MockitoBean
    private MetricsService metricsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private final String APIURL = "/api/user/metrics";
    
    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void createValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        
        mockMvc.perform(post(APIURL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(metricsDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        when(metricsService.getMetrics(new Email("finn@thehuman.com"), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(metricsDTO);
        
        MetricsDTO updatedMetricsDTO = new MetricsDTO();
        updatedMetricsDTO.setDate(metricsDTO.getDate());
        updatedMetricsDTO.setMaxGoodTimeSeconds(metricsDTO.getMaxGoodTimeSeconds() + 1);
        updatedMetricsDTO.setCurrentGoodTimeSeconds(metricsDTO.getCurrentGoodTimeSeconds() + 1);
        updatedMetricsDTO.setMaxBadTimeSeconds(metricsDTO.getMaxBadTimeSeconds() + 1);
        updatedMetricsDTO.setCurrentBadTimeSeconds(metricsDTO.getCurrentBadTimeSeconds() + 1);
        
        mockMvc.perform(getPutRequestBuilder(APIURL, updatedMetricsDTO))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void readValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        when(metricsService.getMetrics(new Email("finn@thehuman.com"), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(metricsDTO);

        mockMvc.perform(get(APIURL)
                        .param("date", metricsDTO.getDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxGoodTimeSeconds").value(metricsDTO.getMaxGoodTimeSeconds()))
                .andExpect(jsonPath("$.currentGoodTimeSeconds").value(metricsDTO.getCurrentGoodTimeSeconds()))
                .andExpect(jsonPath("$.maxBadTimeSeconds").value(metricsDTO.getMaxBadTimeSeconds()))
                .andExpect(jsonPath("$.currentBadTimeSeconds").value(metricsDTO.getCurrentBadTimeSeconds()));
    }
}