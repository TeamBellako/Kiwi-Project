package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.*;
import com.kiwi.features.skills.exceptions.DeckSlotAlreadyOccupiedException;
import com.kiwi.features.skills.exceptions.SkillLevelUpNotFoundException;
import com.kiwi.features.skills.exceptions.SkillNotFoundException;
import com.kiwi.features.skills.exceptions.UserSkillStatusNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillStatusRepository userSkillStatusRepository;
    private final SkillProgressService progress;

    public SkillService(
            SkillRepository skillRepository,
            UserSkillStatusRepository userSkillStatusRepository,
            SkillProgressService progress
    ) {
        this.skillRepository = skillRepository;
        this.userSkillStatusRepository = userSkillStatusRepository;
        this.progress = progress;
    }

    // ============================================================================================
    // GET SKILLS
    // ============================================================================================

    @Transactional
    public List<SkillDTO> getAllSkillsForUser(Long userId) {

        List<UserSkillStatusPersistence> statuses =
                userSkillStatusRepository.findByIdUserId(userId);

        List<SkillDomain> domains = statuses.stream()
                .map(this::buildSkillDomain)
                .toList();

        for (int i = 0; i < domains.size(); i++) {

            SkillDomain skill = domains.get(i);

            if (skill.getCooldownType() == CooldownType.TIME && skill.isCooldown()) {

                SkillDomain updated = progress.updateCooldown(skill);

                if (!updated.isCooldown()) {

                    UserSkillStatusPersistence status = statuses.get(i);

                    UserSkillStatusPersistence updatedStatus =
                            SkillMapper.toPersistence(
                                    userId,
                                    updated,
                                    status.getSkill()
                            );

                    userSkillStatusRepository.save(updatedStatus);
                }
            }
        }

        return domains.stream()
                .map(SkillMapper::toDTO)
                .toList();
    }

    // ============================================================================================
    // GIVE / LEVEL UP
    // ============================================================================================

    @Transactional
    public SkillDTO giveSkillToUser(Long userId, Long skillId) {

        SkillPersistence skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException(skillId));

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElse(null);

        SkillDomain given = progress.giveSkill(skill, status);

        UserSkillStatusPersistence givenStatus = SkillMapper.toPersistence(userId, given, skill);
        userSkillStatusRepository.saveAndFlush(givenStatus);

        return SkillMapper.toDTO(buildSkillDomain(givenStatus));
    }

    @Transactional
    public SkillDTO levelUpSkill(Long userId, Long skillId) {

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain current = buildSkillDomain(status);

        if(current.getLevelupSkillId() == null){
            throw new SkillLevelUpNotFoundException(skillId);
        }

        return giveSkillToUser(userId, current.getLevelupSkillId());
    }

    // ============================================================================================
    // COOLDOWN
    // ============================================================================================

    @Transactional
    public SkillDTO putSkillOnCooldown(Long userId, Long skillId) {

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain current = buildSkillDomain(status);
        SkillDomain updated = progress.putOnCooldown(current);

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, status.getSkill())
        );

        return SkillMapper.toDTO(updated);
    }

    @Transactional
    public SkillDTO removeCooldown(Long userId, Long skillId) {

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain current = buildSkillDomain(status);
        SkillDomain updated = progress.removeCooldown(current);

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, status.getSkill())
        );

        return SkillMapper.toDTO(updated);
    }

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    public SkillDTO equipSkill(Long userId, long skillId, EquipSkillDTO equipSkillDTO) {

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        Optional<UserSkillStatusPersistence> skillInDeckSlot =
                userSkillStatusRepository
                        .findByIdUserIdAndDeckSlot(userId, equipSkillDTO.getDeckSlot());

        if(skillInDeckSlot.isPresent()){
            throw new DeckSlotAlreadyOccupiedException(skillId, equipSkillDTO.getDeckSlot());
        }

        SkillDomain current = buildSkillDomain(status);
        SkillDomain updated = progress.equipSkill(current, equipSkillDTO.getDeckSlot());

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, status.getSkill())
        );

        return SkillMapper.toDTO(updated);
    }

    public SkillDTO unequipSkill(Long userId, long skillId) {

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain current = buildSkillDomain(status);
        SkillDomain updated = progress.unequipSkill(current);

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, status.getSkill())
        );

        return SkillMapper.toDTO(updated);
    }

    // ============================================================================================
    // HELPERS
    // ============================================================================================

    private SkillDomain buildSkillDomain(UserSkillStatusPersistence status) {
        return SkillMapper.toDomain(status.getSkill(), status);
    }
}
