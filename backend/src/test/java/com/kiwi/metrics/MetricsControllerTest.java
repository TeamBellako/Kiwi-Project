package com.kiwi.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.metrics.MetricsController;
import com.kiwi.features.metrics.MetricsDTO;
import com.kiwi.features.metrics.MetricsFactory;
import com.kiwi.features.metrics.MetricsService;
import com.kiwi.utils.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.users.CustomUserDetailsService;
import com.kiwi.types.Email;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static com.kiwi.utils.HTTPTestUtils.getPutRequestBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MetricsController.class)
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JacksonConfig.class})
public class MetricsControllerTest {
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
        when(metricsService.getMetricsByEmailAndDate(new Email("finn@thehuman.com"), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(Optional.of(metricsDTO));
        
        MetricsDTO updatedMetricsDTO = metricsDTO.copy();
        updatedMetricsDTO.setSteps(metricsDTO.getSteps() + 1);
        
        mockMvc.perform(getPutRequestBuilder(APIURL, updatedMetricsDTO))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void readValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        when(metricsService.getMetricsByEmailAndDate(new Email("finn@thehuman.com"), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(Optional.of(metricsDTO));

        mockMvc.perform(get(APIURL)
                        .param("date", metricsDTO.getDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps").value(metricsDTO.getSteps()));
    }
}