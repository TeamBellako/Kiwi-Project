package com.kiwi.skills;

import com.kiwi.features.skills.controllers.SkillService;
import com.kiwi.features.skills.controllers.SkillProgressService;
import com.kiwi.features.skills.data.DTO.SkillDTO;
import com.kiwi.features.skills.data.persistence.UserSkillStatusPersistence;
import com.kiwi.features.skills.exceptions.DeckSlotAlreadyOccupiedException;
import com.kiwi.features.skills.exceptions.SkillLevelUpNotFoundException;
import com.kiwi.features.skills.exceptions.SkillNotFoundException;
import com.kiwi.features.skills.exceptions.UserSkillStatusNotFoundException;
import org.junit.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static com.kiwi.skills.SkillTestFactory.*;

public class SkillServiceTests {

    private final SkillTestRepositoryInMemory skillRepo =
            new SkillTestRepositoryInMemory();

    private final UserSkillStatusTestRepositoryInMemory userSkillStatusRepo =
            new UserSkillStatusTestRepositoryInMemory();

    private final SkillProgressService skillProgress =
            new SkillProgressService();

    private final SkillEffectTestRepositoryInMemory skillEffectRepo =
            new SkillEffectTestRepositoryInMemory();

    private final EnemySkillTestRepositoryInMemory enemySkillRepo =
            new EnemySkillTestRepositoryInMemory();

    private final SkillService service =
            new SkillService(skillRepo, userSkillStatusRepo, skillProgress, skillEffectRepo, enemySkillRepo, event -> {});

    private final Long userId = 1L;

    // ============================================================================================
    // GET SKILLS
    // ============================================================================================

    @Test
    public void getAllSkillsForUser() {

        var skill1 = skillRepo.saveAndFlush(persistenceSkill(1L));
        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));

        userSkillStatusRepo.saveAndFlush(equippedSkill(userId, skill1));
        userSkillStatusRepo.saveAndFlush(unEquippedSkill(userId, skill2));

        var result = service.getAllSkillsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    public void getAllSkillsForUser_expiredCooldown_isRemoved() {

        // GIVEN
        var skill = persistenceSkill(1L);
        skillRepo.saveAndFlush(skill);

        UserSkillStatusPersistence status = cooldownSkill(userId, skill);
        // forced expired cooldown
        status.setCooldownUntil(Instant.now().minusSeconds(60));

        userSkillStatusRepo.saveAndFlush(status);

        var result = service.getAllSkillsForUser(userId);

        assertEquals(1, result.size());

        SkillDTO dto = result.get(0);
        assertFalse(dto.isCooldown());
        assertNull(dto.getCooldownUntil());

        UserSkillStatusPersistence updated =
                userSkillStatusRepo
                        .findByIdUserIdAndIdSkillId(userId, skill.getId())
                        .orElseThrow();

        assertFalse(updated.isCooldown());
        assertNull(updated.getCooldownUntil());
    }

    // ============================================================================================
    // GIVE / LEVEL UP
    // ============================================================================================

    @Test
    public void giveSkill_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        var result = service.giveSkillToUser(userId, skill.getId());

        assertNotNull(result);
        assertEquals(skill.getId(), result.getSkillId());
    }

    @Test(expected = SkillNotFoundException.class)
    public void giveSkill_notFound() {
        service.giveSkillToUser(userId, 999L);
    }

    // ============================================================================================
    // LEVEL UP
    // ============================================================================================

    @Test()
    public void levelUpSkill_success() {

        var skill1 = persistenceSkill(1L);
        var skill2 = persistenceSkill(2L);
        skill2.setLevelupSkillId(1L);
        skillRepo.saveAndFlush(skill1);
        skillRepo.saveAndFlush(skill2);

        userSkillStatusRepo.saveAndFlush(
                unEquippedSkill(userId, skill2)
        );

        var result = service.levelUpSkill(userId, skill2.getId());
        assertEquals(skill1.getId(), result.getSkillId());
    }

    @Test(expected = SkillLevelUpNotFoundException.class)
    public void levelUpSkill_noNextSkillFails() {

        var skill = persistenceSkill(1L);
        skill.setLevelupSkillId(null);
        skillRepo.saveAndFlush(skill);

        userSkillStatusRepo.saveAndFlush(
                unEquippedSkill(userId, skill)
        );

        service.levelUpSkill(userId, skill.getId());
    }

    // ============================================================================================
    // COOLDOWN
    // ============================================================================================

    @Test
    public void putSkillOnCooldown_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));
        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill)
        );

        var result = service.putSkillOnCooldown(userId, skill.getId());

        assertTrue(result.isCooldown());
        assertNotNull(result.getCooldownUntil());
    }

    @Test(expected = UserSkillStatusNotFoundException.class)
    public void putSkillOnCooldown_noStatusFails() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));
        service.putSkillOnCooldown(userId, skill.getId());
    }

    @Test
    public void removeCooldownOfSkill_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));
        userSkillStatusRepo.saveAndFlush(
                cooldownSkill(userId, skill)
        );

        var result = service.removeCooldown(userId, skill.getId());

        assertFalse(result.isCooldown());
        assertNull(result.getCooldownUntil());
    }

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    @Test
    public void equipSkill_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                unEquippedSkill(userId, skill)
        );

        var result = service.equipSkill(userId, skill.getId(), SkillTestFactory.equipSkillDTO(1));

        assertEquals(1, result.getDeckSlot());
    }

    @Test(expected = UserSkillStatusNotFoundException.class)
    public void equipSkill_noStatusFails() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        service.equipSkill(userId, skill.getId(), SkillTestFactory.equipSkillDTO(1));
    }

    @Test(expected = DeckSlotAlreadyOccupiedException.class)
    public void equipSkill_deckSlotAlreadyOccupiedFails() {

        var skill1 = skillRepo.saveAndFlush(persistenceSkill(1L));
        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill1)
        );

        userSkillStatusRepo.saveAndFlush(
                unEquippedSkill(userId, skill2)
        );

        service.equipSkill(userId, skill2.getId(), SkillTestFactory.equipSkillDTO(1));
    }

    // ============================================================================================
    // UNEQUIP
    // ============================================================================================

    @Test
    public void unequipSkill_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill)
        );

        var result = service.unequipSkill(userId, skill.getId());

        assertEquals(0, result.getDeckSlot());
    }

    @Test(expected = UserSkillStatusNotFoundException.class)
    public void unequipSkill_noStatusFails() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        service.unequipSkill(userId, skill.getId());
    }

    // ============================================================================================
    // SKILL COMBAT EFFECTS
    // ============================================================================================

    @Test
    public void getCombatSkillsForUser_returnsSkillsWithEffects() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill)
        );

        skillEffectRepo.save(skillEffect1(skill.getId()));
        skillEffectRepo.save(skillEffect2(skill.getId()));

        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill2)
        );

        skillEffectRepo.save(skillEffect2(skill2.getId()));

        var result = service.getCombatSkillsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    public void getCombatSkillsForEnemy_returnsSkillsWithEffects() {

        Long enemyId = 99L;

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));
        skillEffectRepo.save(skillEffect1(skill.getId()));

        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));
        skillEffectRepo.save(skillEffect2(skill2.getId()));

        enemySkillRepo.save(enemySkill(enemyId, skill));
        enemySkillRepo.save(enemySkill(enemyId, skill2));

        var result = service.getCombatSkillsForEnemy(enemyId);

        assertEquals(2, result.size());
    }

    @Test
    public void getCombatSkillsForUser_skillWithoutEffects() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill)
        );

        var result = service.getCombatSkillsForUser(userId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getEffects().isEmpty());
    }

    @Test
    public void getCombatSkillsForUser_skillWithMultipleEffects() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                equippedSkill(userId, skill)
        );

        skillEffectRepo.save(skillEffect1(skill.getId()));
        skillEffectRepo.save(skillEffect2(skill.getId()));

        var result = service.getCombatSkillsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getEffects().size());
    }

    @Test
    public void getCombatSkillsForEnemy_noSkills() {

        Long enemyId = 99L;

        var result = service.getCombatSkillsForEnemy(enemyId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getCombatSkillsForUser_noEquippedSkills() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        userSkillStatusRepo.saveAndFlush(
                unEquippedSkill(userId, skill)
        );

        var result = service.getCombatSkillsForUser(userId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void getCombatSkillsForUser_ignoresEffectsFromOtherSkills() {

        var skill1 = skillRepo.saveAndFlush(persistenceSkill(1L));

        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));
        skillEffectRepo.save(skillEffect1(skill2.getId()));

        userSkillStatusRepo.saveAndFlush(equippedSkill(userId, skill1));

        var result = service.getCombatSkillsForUser(userId);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getEffects().isEmpty());
    }

}
