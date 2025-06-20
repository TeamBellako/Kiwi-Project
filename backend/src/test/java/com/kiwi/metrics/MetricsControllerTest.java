package com.kiwi.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.GlobalExceptionHandler;
import com.kiwi.common.JacksonConfig;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.security.WebSecurityConfig;
import com.kiwi.users.CustomUserDetailsService;
import com.kiwi.users.Email;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
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
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static com.kiwi.utils.HTTPTestUtils.getPutRequestBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    public void updateValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        when(metricsService.getMetricsByEmailAndDate(new Email(metricsDTO.getEmail()), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(Optional.of(metricsDTO));
        
        MetricsDTO updatedMetricsDTO = metricsDTO;
        updatedMetricsDTO.setSteps(metricsDTO.getSteps() + 1);
        
        mockMvc.perform(getPutRequestBuilder(APIURL, updatedMetricsDTO))
                .andExpect(status().isOk());
        Optional<MetricsDTO> retrievedUpdatedMetricsDTO = 
                metricsService.getMetricsByEmailAndDate(new Email(metricsDTO.getEmail()), LocalDate.parse(metricsDTO.getDate()));
        
        assert(retrievedUpdatedMetricsDTO.isPresent());
        assertEquals(MetricsMapper.toDomain(updatedMetricsDTO), MetricsMapper.toDomain(retrievedUpdatedMetricsDTO.get()));
        assertNotEquals(MetricsMapper.toDomain(metricsDTO), MetricsMapper.toDomain(retrievedUpdatedMetricsDTO.get()));
    }

    @Test
    public void readValidMetrics() throws Exception {
        MetricsDTO metricsDTO = MetricsFactory.generateRandomValidMetricDTO();
        when(metricsService.getMetricsByEmailAndDate(new Email(metricsDTO.getEmail()), LocalDate.parse(metricsDTO.getDate())))
                .thenReturn(Optional.of(metricsDTO));
        
        mockMvc.perform(get(APIURL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps").value(metricsDTO.getSteps()));
    }
}