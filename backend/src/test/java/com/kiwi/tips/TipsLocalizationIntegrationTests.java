package com.kiwi.tips;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.tips.controllers.TipsRepository;
import com.kiwi.features.tips.data.TipPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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
@Import({GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class})
public class TipsLocalizationIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsersRepository usersRepository;
    @Autowired private TipsRepository tipsRepository;
    @Autowired private JdbcTemplate jdbc;

    @Test
    @WithMockUser(username = "locale@test.com")
    public void getTip_defaultEnglishUser_canSwitchResponseToSpanish() throws Exception {
        usersRepository.saveAndFlush(UsersPersistence.builder()
                .email("locale@test.com")
                .hashedPassword("test")
                .registerDate(LocalDate.now())
                .languagePreference("en")
                .build());
        TipPersistence tip = tipsRepository.saveAndFlush(TipPersistence.builder()
                .title("English title")
                .text("English text")
                .readMoreURL("https://example.com")
                .build());
        jdbc.update("UPDATE tips SET title_es = ?, text_es = ? WHERE id = ?",
                "Titulo en espanol", "Texto en espanol", tip.getId());

        mockMvc.perform(get("/api/tips/{id}", tip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("English title"))
                .andExpect(jsonPath("$.text").value("English text"));

        UsersPersistence user = usersRepository.findByEmail("locale@test.com").orElseThrow();
        user.setLanguagePreference("es");
        usersRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/tips/{id}", tip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titulo en espanol"))
                .andExpect(jsonPath("$.text").value("Texto en espanol"));
    }
}