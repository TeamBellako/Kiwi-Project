package com.kiwi.combat;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.combat.data.enums.BarkTriggerType;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.persistence.CombatBarkTriggerPersistence;
import com.kiwi.features.combat.data.persistence.CombatConfigPersistence;
import com.kiwi.features.combat.data.persistence.CombatFiredBarkKey;
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

import static com.kiwi.combat.CombatTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
public class CombatBarkIntegrationTests {

    private final String API_URL = "/api/combat";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatsRepository userStatsRepository;
    @Autowired private EnemyRepository enemyRepository;
    @Autowired private CombatConfigRepository combatConfigRepository;
    @Autowired private CombatRepository combatRepository;
    @Autowired private CombatBarkTriggerRepository barkTriggerRepository;
    @Autowired private CombatFiredBarkRepository firedBarkRepository;

    // ============================================================
    // HELPERS
    // ============================================================

    private UsersPersistence createUser(String email) {
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence user =
                UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        user.setEmail(email);
        return usersRepository.saveAndFlush(user);
    }

    private CombatScenario createCombatPrerequisites(String email) {

        UsersPersistence user = createUser(email);

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

    private CombatPersistence saveOngoingCombat(CombatScenario scenario) {
        CombatPersistence combat = ongoingCombat(
                scenario.user.getId(),
                scenario.config.getId(),
                scenario.stats,
                scenario.enemy
        );
        combat.setEndsAt(Instant.now().plusSeconds(600));
        return combatRepository.saveAndFlush(combat);
    }

    private CombatBarkTriggerPersistence saveBark(Long configId, BarkTriggerType type) {
        CombatBarkTriggerPersistence trigger = barkTrigger(configId, type, 1L);
        return barkTriggerRepository.saveAndFlush(trigger);
    }

    // ============================================================
    // START / RESUME — DTO INCLUDES barks AND firedBarkIds
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void startCombat_returnsEmptyBarkArrays_whenNoneConfigured() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");

        mockMvc.perform(post(API_URL + "/start/" + scenario.config.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barks").isArray())
                .andExpect(jsonPath("$.barks.length()").value(0))
                .andExpect(jsonPath("$.firedBarkIds").isArray())
                .andExpect(jsonPath("$.firedBarkIds.length()").value(0));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void startCombat_includesConfiguredBarks() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        mockMvc.perform(post(API_URL + "/start/" + scenario.config.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barks.length()").value(1))
                .andExpect(jsonPath("$.barks[0].id").value(trigger.getId()))
                .andExpect(jsonPath("$.barks[0].type").value(BarkTriggerType.ENEMY_HP_PERCENT.name()))
                .andExpect(jsonPath("$.barks[0].threshold").value(50.0))
                .andExpect(jsonPath("$.barks[0].conversationId").value(trigger.getConversationId()))
                .andExpect(jsonPath("$.barks[0].dismissMode").value("AUTO"))
                .andExpect(jsonPath("$.barks[0].priority").value(0))
                .andExpect(jsonPath("$.firedBarkIds.length()").value(0));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getActiveCombat_returnsFiredBarkIds_afterFiring() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);
        firedBarkRepository.saveAndFlush(firedBark(combat.getId(), trigger.getId()));

        mockMvc.perform(get(API_URL + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(combat.getId()))
                .andExpect(jsonPath("$.barks.length()").value(1))
                .andExpect(jsonPath("$.firedBarkIds.length()").value(1))
                .andExpect(jsonPath("$.firedBarkIds[0]").value(trigger.getId()));
    }

    // ============================================================
    // POST .../fired
    // ============================================================

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_persistsAndReturns204() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + trigger.getId() + "/fired"))
                .andExpect(status().isNoContent());

        assertTrue(
                firedBarkRepository.existsById(new CombatFiredBarkKey(combat.getId(), trigger.getId()))
        );
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_isIdempotent() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + trigger.getId() + "/fired"))
                .andExpect(status().isNoContent());
        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + trigger.getId() + "/fired"))
                .andExpect(status().isNoContent());

        assertEquals(1, firedBarkRepository.findById_CombatId(combat.getId()).size());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_returns404_whenCombatMissing() throws Exception {

        createUser("finn@thehuman.com");

        mockMvc.perform(post(API_URL + "/9999/barks/1/fired"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_returns404_forUnknownTrigger() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/9999/fired"))
                .andExpect(status().isNotFound());

        assertFalse(
                firedBarkRepository.existsById(new CombatFiredBarkKey(combat.getId(), 9999L))
        );
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_returns404_whenTriggerBelongsToDifferentConfig() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);

        // A trigger that belongs to another combat config.
        CombatConfigPersistence otherConfig = combatConfig(null, scenario.enemy.getId());
        otherConfig.setId(null);
        combatConfigRepository.saveAndFlush(otherConfig);
        CombatBarkTriggerPersistence foreignTrigger = saveBark(otherConfig.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + foreignTrigger.getId() + "/fired"))
                .andExpect(status().isNotFound());

        assertFalse(
                firedBarkRepository.existsById(new CombatFiredBarkKey(combat.getId(), foreignTrigger.getId()))
        );
    }

    @Test
    @WithMockUser(username = "intruder@thehuman.com")
    public void markBarkFired_returns404_whenCombatBelongsToAnotherUser() throws Exception {

        // Combat owner.
        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = saveOngoingCombat(scenario);
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        // Logged-in user is someone else; create them so user lookup succeeds.
        createUser("intruder@thehuman.com");

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + trigger.getId() + "/fired"))
                .andExpect(status().isNotFound());

        assertFalse(
                firedBarkRepository.existsById(new CombatFiredBarkKey(combat.getId(), trigger.getId()))
        );
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void markBarkFired_returns404_whenCombatNotOngoing() throws Exception {

        CombatScenario scenario = createCombatPrerequisites("finn@thehuman.com");
        CombatPersistence combat = ongoingCombat(
                scenario.user.getId(),
                scenario.config.getId(),
                scenario.stats,
                scenario.enemy
        );
        combat.setCombatStatus(CombatGeneralStatus.USER_WON);
        combat.setEndsAt(Instant.now().plusSeconds(600));
        combat = combatRepository.saveAndFlush(combat);
        CombatBarkTriggerPersistence trigger = saveBark(scenario.config.getId(), BarkTriggerType.ENEMY_HP_PERCENT);

        mockMvc.perform(post(API_URL + "/" + combat.getId() + "/barks/" + trigger.getId() + "/fired"))
                .andExpect(status().isNotFound());
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
