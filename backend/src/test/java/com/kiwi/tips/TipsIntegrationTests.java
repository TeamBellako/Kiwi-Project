package com.kiwi.tips;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.tips.controllers.TipsRepository;
import com.kiwi.features.tips.data.TipPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class })
public class TipsIntegrationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private TipsRepository tipsRepository;
    @Autowired private ObjectMapper objectMapper;
    
    private final TipPersistence targetTip = new TipPersistence(1L, "Pomodoro Timer", "Just work, bro", "https://www.todoist.com/es/productivity-methods/pomodoro-technique");

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getTip() throws Exception {
        mockMvc.perform(get("/api/tips/" + targetTip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(targetTip.getTitle()))
                .andExpect(jsonPath("$.text").value(targetTip.getText()))
                .andExpect(jsonPath("$.readMoreURL").value(targetTip.getReadMoreURL()));
    }
}
