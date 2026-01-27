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

    public List<SkillDTO> getAllSkillsForUser(Long userId) {
        return userSkillStatusRepository.findByIdUserId(userId).stream()
                .map(this::buildSkillDomain)
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

        SkillPersistence skill = skillRepository.findById(skillId).orElseThrow();

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain updated =
                progress.putOnCooldown(SkillMapper.toDomain(skill, status));

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, skill)
        );

        return SkillMapper.toDTO(updated);
    }

    @Transactional
    public SkillDTO removeCooldown(Long userId, Long skillId) {

        SkillPersistence skill = skillRepository.findById(skillId).orElseThrow();

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain updated =
                progress.removeCooldown(SkillMapper.toDomain(skill, status));

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, skill)
        );

        return SkillMapper.toDTO(updated);
    }

    // ============================================================================================
    // EQUIP
    // ============================================================================================

    public SkillDTO equipSkill(Long userId, long skillId, int deckSlot) {

        SkillPersistence skill = skillRepository.findById(skillId).orElseThrow();

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        Optional<UserSkillStatusPersistence> skillInDeckSlot =
                userSkillStatusRepository
                        .findByIdUserIdAndDeckSlot(userId, deckSlot);

        if(skillInDeckSlot.isPresent()){
            throw new DeckSlotAlreadyOccupiedException(skillId, deckSlot);
        }

        SkillDomain updated =
                progress.equipSkill(SkillMapper.toDomain(skill, status), deckSlot);

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, skill)
        );

        return SkillMapper.toDTO(updated);
    }

    public SkillDTO unequipSkill(Long userId, long skillId) {

        SkillPersistence skill = skillRepository.findById(skillId).orElseThrow();

        UserSkillStatusPersistence status =
                userSkillStatusRepository
                        .findByIdUserIdAndIdSkillId(userId, skillId)
                        .orElseThrow(() -> new UserSkillStatusNotFoundException(userId, skillId));

        SkillDomain updated =
                progress.unequipSkill(SkillMapper.toDomain(skill, status));

        userSkillStatusRepository.saveAndFlush(
                SkillMapper.toPersistence(userId, updated, skill)
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
