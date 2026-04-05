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
    public void getAllSkillsForUser_expiredCooldown_isRemoved() {

        // GIVEN
        var skill = persistenceSkill(1L);
        skillRepo.saveAndFlush(skill);

        UserSkillStatusPersistence status = cooldownSkill(userId, skill);
        // forced expired cooldown
        status.setCooldownUntil(Instant.now().minusSeconds(60));

        statusRepo.saveAndFlush(status);

        var result = service.getAllSkillsForUser(userId);

        assertEquals(1, result.size());

        SkillDTO dto = result.get(0);
        assertFalse(dto.isCooldown());
        assertNull(dto.getCooldownUntil());

        UserSkillStatusPersistence updated =
                statusRepo
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

        statusRepo.saveAndFlush(
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

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    @Test
    public void equipSkill_success() {

        var skill = skillRepo.saveAndFlush(persistenceSkill(1L));

        statusRepo.saveAndFlush(
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

        statusRepo.saveAndFlush(
                equippedSkill(userId, skill1)
        );

        statusRepo.saveAndFlush(
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

        statusRepo.saveAndFlush(
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
}
