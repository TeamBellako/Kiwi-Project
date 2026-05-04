package com.kiwi.combat;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;
import com.kiwi.features.combat.data.persistence.CombatPersistence;
import com.kiwi.features.combat.data.persistence.EnemyPersistence;
import com.kiwi.features.combat.data.persistence.UserStatsPersistence;
import com.kiwi.features.combat.repositories.*;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.kiwi.combat.CombatTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
public class CombatIntegrationTests {

    private final String API_URL = "/api/combat";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatsRepository userStatsRepository;
    @Autowired private EnemyRepository enemyRepository;
    @Autowired private CombatConfigRepository combatConfigRepository;
    @Autowired private CombatRepository combatRepository;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser() {
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence user =
                UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        return usersRepository.saveAndFlush(user);
    }

    private CombatScenario createCombatPrerequisites() {

        UsersPersistence user = createUser();

        UserStatsPersistence stats = userStats(user.getId());
        userStatsRepository.saveAndFlush(stats);

        EnemyPersistence enemyEntity = enemy(null);
        enemyEntity.setId(null);
        enemyRepository.saveAndFlush(enemyEntity);

        CombatConfigPersistence config = combatConfig(null, enemyEntity.getId());
        config.setId(null);
        combatConfigRepository.saveAndFlush(config);

        return new CombatScenario(user, stats, enemyEntity, config);
    }

    private CombatPersistence saveCombat(
            CombatScenario scenario,
            CombatGeneralStatus status,
            Instant endsAt
    ) {
        CombatPersistence combat = ongoingCombat(
                scenario.user.getId(),
                scenario.config.getId(),
                scenario.stats,
                scenario.enemy
        );
        combat.setCombatStatus(status);
        combat.setEndsAt(endsAt);
        return combatRepository.saveAndFlush(combat);
    }

    // ============================================================
    // START / RESUME
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void startCombat_createsNewCombat() throws Exception {

        CombatScenario scenario = createCombatPrerequisites();

        mockMvc.perform(post(API_URL + "/start/" + scenario.config.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.combatConfigId").value(scenario.config.getId()))
                .andExpect(jsonPath("$.combatStatus").value(CombatGeneralStatus.ONGOING.name()))
                .andExpect(jsonPath("$.turnNumber").value(1))
                .andExpect(jsonPath("$.enemyName").value(scenario.enemy.getName()));

        assertEquals(1, combatRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void startCombat_resumesExistingOngoing() throws Exception {

        CombatScenario scenario = createCombatPrerequisites();
        CombatPersistence existing = saveCombat(scenario, CombatGeneralStatus.ONGOING, Instant.now().plusSeconds(600));

        mockMvc.perform(post(API_URL + "/start/" + scenario.config.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId()));

        assertEquals(1, combatRepository.findAll().size());
    }

    // ============================================================
    // GET ACTIVE
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getActiveCombat_returnsOngoing() throws Exception {

        CombatScenario scenario = createCombatPrerequisites();
        CombatPersistence existing = saveCombat(scenario, CombatGeneralStatus.ONGOING, Instant.now().plusSeconds(600));

        mockMvc.perform(get(API_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existing.getId()))
                .andExpect(jsonPath("$.combatStatus").value(CombatGeneralStatus.ONGOING.name()));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getActiveCombat_returnsNoContentWhenNone() throws Exception {

        createUser();

        mockMvc.perform(get(API_URL + "/active"))
                .andExpect(status().isNoContent());
    }

    // ============================================================
    // ABANDON
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void abandonCombat_marksAsLostAndResetsStats() throws Exception {

        CombatScenario scenario = createCombatPrerequisites();
        CombatPersistence existing = saveCombat(scenario, CombatGeneralStatus.ONGOING, Instant.now().plusSeconds(600));

        mockMvc.perform(post(API_URL + "/" + existing.getId() + "/abandon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.combatId").value(existing.getId()))
                .andExpect(jsonPath("$.combatStatus").value(CombatGeneralStatus.USER_LOST.name()));

        CombatPersistence updated = combatRepository.findById(existing.getId()).orElseThrow();
        assertEquals(CombatGeneralStatus.USER_LOST, updated.getCombatStatus());
        // resetStatsToOriginalConfig brings HP back to max
        assertEquals(scenario.stats.getMaxHp(), (int) updated.getUserHp());
        assertEquals(scenario.enemy.getMaxHp(), (int) updated.getEnemyHp());
    }

    // ============================================================
    // TIMEOUT
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void timeOutCombat_marksAsLostWhenExpired() throws Exception {

        CombatScenario scenario = createCombatPrerequisites();
        Instant expired = Instant.now().minus(1, ChronoUnit.MINUTES);
        CombatPersistence existing = saveCombat(scenario, CombatGeneralStatus.ONGOING, expired);

        mockMvc.perform(post(API_URL + "/" + existing.getId() + "/timeout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.combatStatus").value(CombatGeneralStatus.USER_LOST.name()));

        CombatPersistence updated = combatRepository.findById(existing.getId()).orElseThrow();
        assertEquals(CombatGeneralStatus.USER_LOST, updated.getCombatStatus());
    }

    // ============================================================
    // SCENARIO HOLDER
    // ============================================================

    private static class CombatScenario {
        final UsersPersistence user;
        final UserStatsPersistence stats;
        final EnemyPersistence enemy;
        final CombatConfigPersistence config;

        CombatScenario(
                UsersPersistence user,
                UserStatsPersistence stats,
                EnemyPersistence enemy,
                CombatConfigPersistence config
        ) {
            this.user = user;
            this.stats = stats;
            this.enemy = enemy;
            this.config = config;
        }
    }
}
