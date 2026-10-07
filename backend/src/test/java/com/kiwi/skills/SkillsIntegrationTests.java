package com.kiwi.skills;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.controllers.UserSkillStatusRepository;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import com.kiwi.features.localization.LocalizedDataService;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import java.util.List;

import static com.kiwi.skills.SkillTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class SkillsIntegrationTests {

    private final String API_URL = "/api/skills";

    @Autowired private MockMvc mockMvc;
    @Autowired private UsersRepository usersRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private UserSkillStatusRepository statusRepository;
    @Autowired private ObjectMapper objectMapper;
        @Autowired private JdbcTemplate jdbc;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser() {
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence user =
                UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        return usersRepository.saveAndFlush(user);
    }

    private SkillPersistence createSkill(Long id) {
        return skillRepository.saveAndFlush(persistenceSkill(id));
    }

    // ============================================================
    // TESTS
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getAllSkills() throws Exception {

        var user = createUser();
        user.setLanguagePreference("en");
        usersRepository.saveAndFlush(user);
        var s1 = createSkill(1L);
        var s2 = createSkill(2L);
        jdbc.update("UPDATE skills SET name_en = ?, name_es = ?, description_en = ?, description_es = ?, quote_en = ?, quote_es = ?, cooldown_other_description_en = ?, cooldown_other_description_es = ? WHERE id IN (?, ?)",
                "English skill", "Habilidad traducida", "English description", "Descripcion traducida",
                "English quote", "Cita traducida", "English cooldown", "Espera traducida", s1.getId(), s2.getId());
        assertEquals("en", usersRepository.findByEmail(user.getEmail()).orElseThrow().getLanguagePreference());
        assertEquals("English skill", skillRepository.findLocalizedByIds(List.of(s1.getId()))
                .get(0).getName());
        assertEquals("English description", skillRepository.findLocalizedByIds(List.of(s1.getId()))
                .get(0).getDescription());
        assertEquals("English quote", skillRepository.findLocalizedByIds(List.of(s1.getId()))
                .get(0).getQuote());
        assertEquals("English cooldown", skillRepository.findLocalizedByIds(List.of(s1.getId()))
                .get(0).getCooldownOtherDescription());

        statusRepository.saveAndFlush(equippedSkill(user.getId(), s1));
        statusRepository.saveAndFlush(unEquippedSkill(user.getId(), s2));

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("English skill"))
                .andExpect(jsonPath("$[0].description").value("English description"))
                .andExpect(jsonPath("$[0].quote").value("English quote"))
                .andExpect(jsonPath("$[0].cooldownOtherDescription").value("English cooldown"));

        user.setLanguagePreference("es");
        usersRepository.saveAndFlush(user);
        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Habilidad traducida"))
                .andExpect(jsonPath("$[0].description").value("Descripcion traducida"))
                .andExpect(jsonPath("$[0].quote").value("Cita traducida"))
                .andExpect(jsonPath("$[0].cooldownOtherDescription").value("Espera traducida"))
                .andExpect(jsonPath("$[1].name").value("Habilidad traducida"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void giveSkill_success() throws Exception {

        var user = createUser();
        var skill = createSkill(1L);

        mockMvc.perform(post(API_URL + "/" + skill.getId() + "/give"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillId").value(skill.getId()));

        var status = statusRepository
                .findByIdUserIdAndIdSkillId(user.getId(), skill.getId())
                .orElseThrow();

        assertEquals(skill.getId(), status.getSkill().getId());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void putSkillOnCooldown_success() throws Exception {

        var user = createUser();
        var skill = createSkill(1L);

        statusRepository.saveAndFlush(equippedSkill(user.getId(), skill));

        mockMvc.perform(post(API_URL + "/" + skill.getId() + "/cooldown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cooldown").value(true));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void removeSkillCooldown_success() throws Exception {

        var user = createUser();
        var skill = createSkill(1L);

        statusRepository.saveAndFlush(cooldownSkill(user.getId(), skill));

        mockMvc.perform(post(API_URL + "/" + skill.getId() + "/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cooldown").value(false));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void equipSkill_success() throws Exception {

        var user = createUser();
        var skill = createSkill(1L);

        statusRepository.saveAndFlush(
                unEquippedSkill(user.getId(), skill)
        );

        mockMvc.perform(
                        post(API_URL + "/" + skill.getId() + "/equip")
                                .contentType("application/json")
                                .content("1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deckSlot").value(1));

        var status =
                statusRepository
                        .findByIdUserIdAndIdSkillId(user.getId(), skill.getId())
                        .orElseThrow();

        assertEquals(1, status.getDeckSlot());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void unequipSkill_success() throws Exception {

        var user = createUser();
        var skill = createSkill(1L);

        statusRepository.saveAndFlush(
                equippedSkill(user.getId(), skill)
        );

        mockMvc.perform(
                        post(API_URL + "/" + skill.getId() + "/unequip")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deckSlot").value(0));

        var status =
                statusRepository
                        .findByIdUserIdAndIdSkillId(user.getId(), skill.getId())
                        .orElseThrow();

        assertEquals(0, status.getDeckSlot());
    }

}
