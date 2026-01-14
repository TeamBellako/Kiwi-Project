package com.kiwi.skills;

import com.kiwi.features.skills.controllers.SkillService;
import com.kiwi.features.skills.controllers.SkillProgressService;
import com.kiwi.features.skills.exceptions.SkillLevelUpNotFoundException;
import com.kiwi.features.skills.exceptions.SkillNotFoundException;
import com.kiwi.features.skills.exceptions.UserSkillStatusNotFoundException;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.kiwi.skills.SkillTestFactory.*;

public class SkillServiceTests {

    private final SkillTestRepositoryInMemory skillRepo =
            new SkillTestRepositoryInMemory();

    private final UserSkillStatusTestRepositoryInMemory statusRepo =
            new UserSkillStatusTestRepositoryInMemory();

    private final SkillProgressService progress =
            new SkillProgressService();

    private final SkillService service =
            new SkillService(skillRepo, statusRepo, progress);

    private final Long userId = 1L;

    // ============================================================================================
    // GET SKILLS
    // ============================================================================================

    @Test
    public void getAllSkillsForUser() {

        var skill1 = skillRepo.saveAndFlush(persistenceSkill(1L));
        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));

        statusRepo.saveAndFlush(equippedSkill(userId, skill1));
        statusRepo.saveAndFlush(unEquippedSkill(userId, skill2));

        var result = service.getAllSkillsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    public void getEquippedSkillsForUser() {

        var skill1 = skillRepo.saveAndFlush(persistenceSkill(1L));
        var skill2 = skillRepo.saveAndFlush(persistenceSkill(2L));

        statusRepo.saveAndFlush(equippedSkill(userId, skill1));
        statusRepo.saveAndFlush(unEquippedSkill(userId, skill2));

        var result = service.getEquippedSkillsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(skill1.getId(), result.get(0).getSkillId());
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
        skill1.setLevelupSkillId(skill2.getId());
        skillRepo.saveAndFlush(skill2);
        skillRepo.saveAndFlush(skill1);

        statusRepo.saveAndFlush(
                unEquippedSkill(userId, skill1)
        );

        var result = service.levelUpSkill(userId, skill1.getId());
        assertEquals(skill2.getId(), result.getSkillId());
    }

    @Test(expected = SkillLevelUpNotFoundException.class)
    public void levelUpSkill_noNextSkillFails() {

        var skill = persistenceSkill(1L);
        skill.setLevelupSkillId(null);
        skillRepo.saveAndFlush(skill);

        statusRepo.saveAndFlush(
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
        statusRepo.saveAndFlush(
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
        statusRepo.saveAndFlush(
                cooldownSkill(userId, skill)
        );

        var result = service.removeCooldown(userId, skill.getId());

        assertFalse(result.isCooldown());
        assertNull(result.getCooldownUntil());
    }
}
