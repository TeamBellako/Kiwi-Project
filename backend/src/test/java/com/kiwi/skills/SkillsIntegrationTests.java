package com.kiwi.skills;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.controllers.UserSkillStatusRepository;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
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

import static com.kiwi.skills.SkillTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.assertEquals;
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
        var s1 = createSkill(1L);
        var s2 = createSkill(2L);

        statusRepository.saveAndFlush(equippedSkill(user.getId(), s1));
        statusRepository.saveAndFlush(unEquippedSkill(user.getId(), s2));

        mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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
