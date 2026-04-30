package com.kiwi.features.skills.controllers;

import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import com.kiwi.features.skills.data.domain.SkillDomain;
import com.kiwi.features.skills.data.mappers.SkillCombatMapper;
import com.kiwi.features.skills.data.mappers.SkillMapper;
import com.kiwi.features.skills.data.persistence.EnemySkillPersistence;
import com.kiwi.features.skills.data.persistence.SkillEffectPersistence;
import com.kiwi.features.skills.data.persistence.SkillPersistence;
import com.kiwi.features.skills.data.persistence.UserSkillStatusPersistence;
import com.kiwi.features.skills.data.DTO.EquipSkillDTO;
import com.kiwi.features.skills.data.DTO.SkillDTO;
import com.kiwi.features.skills.data.enums.CooldownType;
import com.kiwi.features.skills.events.SkillGivenEvent;
import com.kiwi.features.skills.exceptions.DeckSlotAlreadyOccupiedException;
import com.kiwi.features.skills.exceptions.SkillLevelUpNotFoundException;
import com.kiwi.features.skills.exceptions.SkillNotFoundException;
import com.kiwi.features.skills.exceptions.UserSkillStatusNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillStatusRepository userSkillStatusRepository;
    private final SkillProgressService skillProgressService;
    private final SkillEffectRepository skillEffectRepository;
    private final EnemySkillRepository enemySkillRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SkillService(
            SkillRepository skillRepository,
            UserSkillStatusRepository userSkillStatusRepository,
            SkillProgressService skillProgressService, SkillEffectRepository skillEffectRepository, EnemySkillRepository enemySkillRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.skillRepository = skillRepository;
        this.userSkillStatusRepository = userSkillStatusRepository;
        this.skillProgressService = skillProgressService;
        this.skillEffectRepository = skillEffectRepository;
        this.enemySkillRepository = enemySkillRepository;
        this.eventPublisher = eventPublisher;
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

                SkillDomain updated = skillProgressService.updateCooldown(skill);

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

    //------------------------------------------------------------------------------------

    @Transactional
    public List<SkillCombatDomain> getCombatSkillsForUser(Long userId) {

        List<UserSkillStatusPersistence> equippedSkills =
                userSkillStatusRepository
                        .findByIdUserIdAndDeckSlotGreaterThan(userId, 0);

        List<Long> skillIds = equippedSkills.stream()
                .map(s -> s.getId().getSkillId())
                .toList();

        Map<Long, List<SkillEffectPersistence>> effectsBySkill =
                skillEffectRepository.findBySkillIdIn(skillIds)
                        .stream()
                        .collect(Collectors.groupingBy(SkillEffectPersistence::getSkillId));

        return equippedSkills.stream()
                .map(status -> {

                    SkillPersistence skill = status.getSkill();

                    List<SkillEffectPersistence> effects =
                            effectsBySkill.getOrDefault(skill.getId(), List.of());

                    return SkillCombatMapper.toDomain(skill, effects);
                })
                .toList();
    }

    @Transactional
    public List<SkillCombatDomain> getCombatSkillsForEnemy(Long enemyId) {

        List<EnemySkillPersistence> enemySkills = enemySkillRepository.findByEnemy_Id(enemyId);

        List<SkillPersistence> skills =  enemySkills.stream()
                .map(EnemySkillPersistence::getSkill)
                .toList();

        List<Long> skillIds = skills.stream()
                .map(SkillPersistence::getId)
                .toList();

        Map<Long, List<SkillEffectPersistence>> effectsBySkill =
                skillEffectRepository.findBySkillIdIn(skillIds)
                        .stream()
                        .collect(Collectors.groupingBy(SkillEffectPersistence::getSkillId));

        return skills.stream()
                .map(skill -> {

                    List<SkillEffectPersistence> effects =
                            effectsBySkill.getOrDefault(skill.getId(), List.of());

                    return SkillCombatMapper.toDomain(skill, effects);
                })
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

        SkillDomain given = skillProgressService.giveSkill(skill, status);

        UserSkillStatusPersistence givenStatus = SkillMapper.toPersistence(userId, given, skill);
        userSkillStatusRepository.saveAndFlush(givenStatus);

        Long cooldownGoalId = skill.getCooldownType() == CooldownType.GOAL
                ? skill.getCooldownGoalId()
                : null;
        eventPublisher.publishEvent(new SkillGivenEvent(userId, skillId, cooldownGoalId));

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
        SkillDomain updated = skillProgressService.putOnCooldown(current);

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
        SkillDomain updated = skillProgressService.removeCooldown(current);

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
        SkillDomain updated = skillProgressService.equipSkill(current, equipSkillDTO.getDeckSlot());

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
        SkillDomain updated = skillProgressService.unequipSkill(current);

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
