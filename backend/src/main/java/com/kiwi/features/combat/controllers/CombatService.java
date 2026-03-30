package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.mappers.EnemyActorMapper;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.data.mappers.CombatMapper;
import com.kiwi.features.combat.data.mappers.UserActorMapper;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.engine.CombatContextBuilder;
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.combat.engine.SkillRuntime;
import com.kiwi.features.combat.repositories.*;
import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.controllers.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CombatService {

    private final CombatRepository combatRepository;
    private final CombatConfigRepository combatConfigRepository;
    private final UserStatsRepository userStatsRepository;
    private final EnemyRepository enemyRepository;
    private final CombatElementRepository combatElementRepository;
    private final CombatStateRepository combatStateRepository;
    private final UserElementMultiplierRepository userElementMultiplierRepository;
    private final EnemyElementMultiplierRepository enemyElementMultiplierRepository;
    private final UserStatusResistanceRepository userStatusResistanceRepository;
    private final EnemyStatusResistanceRepository enemyStatusResistanceRepository;
    private final CombatStatusEffectRepository combatStatusEffectRepository;
    private final CombatEngine combatEngine;
    private final SkillService skillService;
    private final CombatContextBuilder contextBuilder;

    //------------------------------------------------------------------------------------------------------------------

    public CombatService(
            CombatRepository combatRepository, CombatConfigRepository combatConfigRepository,
            UserStatsRepository userStatsRepository, EnemyRepository enemyRepository,
            CombatElementRepository combatElementRepository, CombatStateRepository combatStateRepository,
            UserElementMultiplierRepository userElementMultiplierRepository,
            EnemyElementMultiplierRepository enemyElementMultiplierRepository,
            UserStatusResistanceRepository userStatusResistanceRepository,
            EnemyStatusResistanceRepository enemyStatusResistanceRepository, CombatStatusEffectRepository combatStatusEffectRepository, CombatEngine combatEngine,
            SkillService skillService, CombatContextBuilder contextBuilder
    ) {
        this.combatRepository = combatRepository;
        this.combatConfigRepository = combatConfigRepository;
        this.userStatsRepository = userStatsRepository;
        this.enemyRepository = enemyRepository;
        this.combatElementRepository = combatElementRepository;
        this.combatStateRepository = combatStateRepository;
        this.userElementMultiplierRepository = userElementMultiplierRepository;
        this.enemyElementMultiplierRepository = enemyElementMultiplierRepository;
        this.userStatusResistanceRepository = userStatusResistanceRepository;
        this.enemyStatusResistanceRepository = enemyStatusResistanceRepository;
        this.combatStatusEffectRepository = combatStatusEffectRepository;
        this.combatEngine = combatEngine;
        this.skillService = skillService;
        this.contextBuilder = contextBuilder;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatDTO startOrResumeCombat(Long userId, Long combatConfigId) {

        UserStatsPersistence userStats =
                userStatsRepository.findById(userId).orElseThrow();

        Optional<CombatPersistence> existingCombat =
                combatRepository.findByUserIdAndCombatConfigId(userId, combatConfigId);

        CombatPersistence combat;

        if(existingCombat.isPresent())
        {
            combat = existingCombat.get();
        }
        else
        {

            CombatConfigPersistence config =
                    combatConfigRepository.findById(combatConfigId).orElseThrow();

            EnemyPersistence enemy =
                    enemyRepository.findById(config.getEnemyId()).orElseThrow();

            combat = CombatPersistence.builder()
                    .combatConfigId(combatConfigId)
                    .userId(userId)
                    .enemyId(enemy.getId())
                    .userHp(userStats.getMaxHp())
                    .enemyHp(enemy.getMaxHp())
                    .turnNumber(1)
                    .combatStatus(CombatGeneralStatus.ONGOING)
                    .endsAt(Instant.now().plus(config.getTimeLimit(), ChronoUnit.MINUTES))
                    .build();

            combatRepository.save(combat);
        }

        Map<Long, CombatElementPersistence> elementMap = loadElementMap();
        Map<Long, CombatStatePersistence> stateMap = loadStateMap();

        EnemyPersistence enemy =
                enemyRepository.findById(combat.getEnemyId()).orElseThrow();

        List<ElementMultiplierDTO> userElements = loadUserElements(userId, elementMap);
        List<StatusResistanceDTO> userResistances = loadUserResistances(userId, stateMap);

        List<ElementMultiplierDTO> enemyElements = loadEnemyElements(enemy.getId(), elementMap);
        List<StatusResistanceDTO> enemyResistances = loadEnemyResistances(enemy.getId(), stateMap);

        UserActorDTO userDTO = UserActorMapper.toDTO(
                userId,
                combat.getUserHp(),
                userStats,
                userElements,
                userResistances
        );

        EnemyActorDTO enemyDTO = EnemyActorMapper.toDTO(
                enemy,
                combat.getEnemyHp(),
                enemyElements,
                enemyResistances
        );

        return CombatMapper.toDTO(combat, userDTO, enemyDTO);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, Long combatId, Long skillId) {

        CombatPersistence combat =
                combatRepository.findById(combatId)
                        .orElseThrow();

        if(combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new IllegalStateException("Combat already finished");
        }

        //A lo mejor pensar que pueda llegar un skill id -1 que sea como pasar turno
        //en el futuro si hay opcion como hablar u otras cosas a lo mejor se pasa la accion y no el id de skill

        // check timeout
        if (combat.getEndsAt() != null) {
            Instant now = Instant.now();

            if (combat.getEndsAt().isBefore(now)) {
                CombatTurnResultDTO result = combatEngine.buildTimeoutCombatTurnResultDTO(userId, combat);

                // save updated combat
                combatRepository.save(combat);

                return result;
            }
        }

        // cooldown
        skillService.putSkillOnCooldown(userId, skillId);

        UserStatsPersistence userStats = userStatsRepository.findById(userId).orElseThrow();

        Map<Long, CombatElementPersistence> elementMap = loadElementMap();
        Map<Long, CombatStatePersistence> stateMap = loadStateMap();

        EnemyPersistence enemy =
                enemyRepository.findById(combat.getEnemyId()).orElseThrow();

        List<ElementMultiplierDTO> userElements = loadUserElements(userId, elementMap);
        List<StatusResistanceDTO> userResistances = loadUserResistances(userId, stateMap);

        List<ElementMultiplierDTO> enemyElements = loadEnemyElements(enemy.getId(), elementMap);
        List<StatusResistanceDTO> enemyResistances = loadEnemyResistances(enemy.getId(), stateMap);

        // TODO obtener los status actuales de cada uno
        //combatStatusEffectRepository

        CombatContext context =
                contextBuilder.build(
                         combat,
                         userStats,
                         enemy,
                        userElements,
                        enemyElements,
                        userResistances,
                        enemyResistances,
                        userSkills,
                        enemySkills,
                        userLastSkillUsed,
                        enemyLastSkillUsed
                );

        CombatTurnResultDTO result =
                combatEngine.executeTurn(context, skillId);

        // persist HP
        combat.setUserHp(context.getUser().getHp());
        combat.setEnemyHp(context.getEnemy().getHp());

        // save updated combat
        combatRepository.save(combat);

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO timeOutCombat(Long userId, Long combatId) {

        CombatPersistence combat =
                combatRepository.findById(combatId)
                        .orElseThrow();

        if(combat.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new IllegalStateException("Combat already finished");
        }

        CombatTurnResultDTO result =
                combatEngine.buildTimeoutCombatTurnResultDTO(userId, combat);

        // save updated combat
        combatRepository.save(combat);

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------
    // AUXILIARY FUNCTIONS
    //------------------------------------------------------------------------------------------------------------------

    private Map<Long, CombatElementPersistence> loadElementMap() {
        return combatElementRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatElementPersistence::getId,
                        Function.identity()
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    private Map<Long, CombatStatePersistence> loadStateMap() {
        return combatStateRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatStatePersistence::getId,
                        Function.identity()
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<ElementMultiplierDTO> loadUserElements(
            Long userId,
            Map<Long, CombatElementPersistence> elementMap
    ) {

        List<UserElementMultiplierPersistence> multipliers =
                userElementMultiplierRepository.findByUserId(userId);

        return multipliers.stream()
                .map(m -> {

                    CombatElementPersistence element =
                            elementMap.get(m.getElementId());

                    return ElementMultiplierDTO.builder()
                            .elementId(element.getId())
                            .elementName(element.getName())
                            .elementIcon(element.getIcon())
                            .multiplier(m.getMultiplier())
                            .build();
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<ElementMultiplierDTO> loadEnemyElements(
            Long enemyId,
            Map<Long, CombatElementPersistence> elementMap
    ) {

        List<EnemyElementMultiplierPersistence> multipliers =
                enemyElementMultiplierRepository.findByEnemyId(enemyId);

        return multipliers.stream()
                .map(m -> {

                    CombatElementPersistence element =
                            elementMap.get(m.getElementId());

                    return ElementMultiplierDTO.builder()
                            .elementId(element.getId())
                            .elementName(element.getName())
                            .elementIcon(element.getIcon())
                            .multiplier(m.getMultiplier())
                            .build();
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<StatusResistanceDTO> loadUserResistances(
            Long userId,
            Map<Long, CombatStatePersistence> stateMap
    ) {

        List<UserStatusResistancePersistence> resistances =
                userStatusResistanceRepository.findByUserId(userId);

        return resistances.stream()
                .map(r -> {

                    CombatStatePersistence state =
                            stateMap.get(r.getStateId());

                    return StatusResistanceDTO.builder()
                            .stateId(state.getId())
                            .stateName(state.getName())
                            .stateIcon(state.getIcon())
                            .resistance(r.getResistance())
                            .build();
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<StatusResistanceDTO> loadEnemyResistances(
            Long enemyId,
            Map<Long, CombatStatePersistence> stateMap
    ) {

        List<EnemyStatusResistancePersistence> resistances =
                enemyStatusResistanceRepository.findByEnemyId(enemyId);

        return resistances.stream()
                .map(r -> {

                    CombatStatePersistence state =
                            stateMap.get(r.getStateId());

                    return StatusResistanceDTO.builder()
                            .stateId(state.getId())
                            .stateName(state.getName())
                            .stateIcon(state.getIcon())
                            .resistance(r.getResistance())
                            .build();
                })
                .toList();
    }

}