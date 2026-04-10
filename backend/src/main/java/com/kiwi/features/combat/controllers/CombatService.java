package com.kiwi.features.combat.controllers;

import com.kiwi.features.combat.data.domain.*;
import com.kiwi.features.combat.data.dto.*;
import com.kiwi.features.combat.data.enums.CombatActorType;
import com.kiwi.features.combat.data.mappers.*;
import com.kiwi.features.combat.data.persistence.*;
import com.kiwi.features.combat.data.enums.CombatGeneralStatus;
import com.kiwi.features.combat.engine.CombatContext;
import com.kiwi.features.combat.engine.CombatEngine;
import com.kiwi.features.combat.repositories.*;
import com.kiwi.features.skills.controllers.SkillService;
import com.kiwi.features.skills.data.domain.SkillCombatDomain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private final CombatStateService combatStateService;
    private final CombatProgressService combatProgressService;
    private final CombatEngine combatEngine;
    private final SkillService skillService;
    private final CombatLogService combatLogService;

    //------------------------------------------------------------------------------------------------------------------

    public CombatService(
            CombatRepository combatRepository, CombatConfigRepository combatConfigRepository,
            UserStatsRepository userStatsRepository, EnemyRepository enemyRepository,
            CombatElementRepository combatElementRepository, CombatStateRepository combatStateRepository,
            UserElementMultiplierRepository userElementMultiplierRepository,
            EnemyElementMultiplierRepository enemyElementMultiplierRepository,
            UserStatusResistanceRepository userStatusResistanceRepository,
            EnemyStatusResistanceRepository enemyStatusResistanceRepository, CombatActiveStatusRepository combatActiveStatusRepository, CombatProgressService combatProgressService, CombatEngine combatEngine,
            SkillService skillService, CombatLogService combatLogService, CombatStateService combatStateService
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
        this.combatProgressService = combatProgressService;
        this.combatEngine = combatEngine;
        this.skillService = skillService;
        this.combatLogService = combatLogService;
        this.combatStateService = combatStateService;
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

        Map<Long, CombatElementPersistence> elementsMap = loadElementsMap();
        Map<Long, CombatStatePersistence> statesMap = loadStatesMap();

        CombatActorDTO userDTO = buildUserCombatActorDTO(userId, combat.getUserHp(), userStats, combat.getId(),
                elementsMap,statesMap);

        EnemyPersistence enemy =
                enemyRepository.findById(combat.getEnemyId()).orElseThrow();

        CombatActorDTO enemyDTO = buildEnemyCombatActorDTO(combat.getEnemyHp(), enemy, combat.getId(),
                elementsMap,statesMap);

        List<CombatActionDTO> log = combatLogService.getCombatLog(combat.getId());

        return CombatMapper.toDTO(combat, userDTO, enemyDTO, enemy.getName(), enemy.getSprite(), log);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO executeTurn(Long userId, Long combatId, Long skillId) {

        CombatPersistence combatPersistence =
                combatRepository.findById(combatId)
                        .orElseThrow();

        if(combatPersistence.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new IllegalStateException("Combat already finished");
        }

        CombatDomain combatDomain = CombatMapper.toDomain(combatPersistence);

        //A lo mejor pensar que pueda llegar un skill id -1 que sea como pasar turno
        //en el futuro si hay opcion como hablar u otras cosas a lo mejor se pasa la accion y no el id de skill

        // check timeout
        if (combatDomain.getEndsAt() != null) {
            combatProgressService.updateTimeOut(combatDomain);

            if(combatDomain.getCombatStatus() != CombatGeneralStatus.ONGOING) {

                // save updated combat
                combatRepository.save(combatPersistence);

                cleanDatabase(userId, combatId);

                return combatEngine.buildTimeoutCombatTurnResultDTO(userId, combatDomain);
            }
        }

        // cooldown
        skillService.putSkillOnCooldown(userId, skillId);

        UserStatsPersistence userStats = userStatsRepository.findById(userId).orElseThrow();

        Map<Long, CombatElementPersistence> elementsMap = loadElementsMap();
        Map<Long, CombatStatePersistence> statesMap = loadStatesMap();


        List<ElementMultiplierDomain> userElements = loadEnemyElements(userId, elementsMap);
        List<StatusResistanceDomain> userResistances = loadEnemyResistances(userId, statesMap);
        List<CombatActiveStatusDomain> userActiveStatus = combatStateService.getActiveStatusDTO(combatId, CombatActorType.ENEMY);
        List<SkillCombatDomain> userSkills = skillService.getCombatSkillsForUser(userId);
        List<Long> userSkillsBlocked = combatLogService.getSkillsBlocked(combatId, CombatActorType.USER);
        Long userLastSkillUsed = combatLogService.getLastSkillUsed(combatId, CombatActorType.USER);

        ActorDomain user = ActorDomainBuilder.buildActorRuntime(
                CombatActorType.USER,
                combatPersistence.getUserHp(),
                userStats,
                userElements,
                userResistances,
                userActiveStatus,
                userSkills,
                userSkillsBlocked,
                userLastSkillUsed
        );

        EnemyPersistence enemyPersistence =
                enemyRepository.findById(combatPersistence.getEnemyId()).orElseThrow();

        List<ElementMultiplierDomain> enemyElements = loadEnemyElements(combatDomain.getEnemyId(), elementsMap);
        List<StatusResistanceDomain> enemyResistances = loadEnemyResistances(combatDomain.getEnemyId(), statesMap);
        List<CombatActiveStatusDomain> enemyActiveStatus = combatStateService.getActiveStatusDTO(combatId, CombatActorType.ENEMY);
        List<SkillCombatDomain> enemySkills = skillService.getCombatSkillsForEnemy(combatPersistence.getEnemyId());
        Long enemyLastSkillUsed = combatLogService.getLastSkillUsed(combatId, CombatActorType.ENEMY);

        List<Long> enemySkillsBlocked = combatLogService.getSkillsBlocked(combatId, CombatActorType.ENEMY);

        ActorDomain enemy = ActorDomainBuilder.buildActorRuntime(
                CombatActorType.ENEMY,
                combatPersistence.getEnemyHp(),
                enemyPersistence,
                enemyElements,
                enemyResistances,
                enemyActiveStatus,
                enemySkills,
                enemySkillsBlocked,
                enemyLastSkillUsed
        );

        CombatContext context = new CombatContext(combatDomain,user,enemy);

        CombatTurnResultDTO result = combatEngine.executeTurn(context, skillId);

        AAAAAAAAAAAAAAA ESTO ES LO QUE HAY QUE REVISAR QUE SE GUARDE BIEN TODO EL LOG A LO MEJOR HAY QUE GUARDAR INFO REDUNDANTE SI NO QUEREMOS VOLVER A BUSCAR LOS STATES Y SKILLS DESDE EL FRONT DE NUEVO
        // save log
        combatLogService.saveCombatActions(result.getActions(),combatId,combatPersistence.getTurnNumber());

        // save last skills
        combatLogService.updateLastSkills(combatId, context.getUser().getLastSkillUsed(), context.getEnemy().getLastSkillUsed());

        // save blocked skills
        combatLogService.syncBlockedSkills(
                combatId,
                new ArrayList<>(context.getUser().getSkills().keySet()),
                new ArrayList<>(context.getEnemy().getSkills().keySet())
        );

        // update combatPersistence
        combatProgressService.applyTurnResult(combatPersistence,combatDomain,context.getUser(),context.getEnemy());

        // save updated combat
        combatRepository.save(combatPersistence);

        if (combatPersistence.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            cleanDatabase(userId, combatId);
        }

        return result;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public CombatTurnResultDTO timeOutCombat(Long userId, Long combatId) {

        CombatPersistence combat =
                combatRepository.findById(combatId)
                        .orElseThrow();

        CombatDomain combatDomain = CombatMapper.toDomain(combat);

        if(combatDomain.getCombatStatus() != CombatGeneralStatus.ONGOING) {
            throw new IllegalStateException("Combat already finished");
        }

        if (combatDomain.getEndsAt() == null) {
            throw new IllegalStateException("No timed combat");
        }

        combatProgressService.updateTimeOut(combatDomain);

        if(combatDomain.getCombatStatus() != CombatGeneralStatus.ONGOING) {

            // save updated combat
            combatRepository.save(combat);

            cleanDatabase(userId, combatId);

            return combatEngine.buildTimeoutCombatTurnResultDTO(userId, combatDomain);
        }

        return new CombatTurnResultDTO();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void cleanDatabase(Long userId, Long combatId) {

        combatLogService.deleteCombatLog(combatId);
        combatLogService.deleteLastSkillsUsed(combatId);
        combatLogService.deleteSkillsBlocked(combatId);
    }


    //------------------------------------------------------------------------------------------------------------------
    // AUXILIARY FUNCTIONS
    //------------------------------------------------------------------------------------------------------------------

    private Map<Long, CombatElementPersistence> loadElementsMap() {
        return combatElementRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatElementPersistence::getId,
                        Function.identity()
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    private Map<Long, CombatStatePersistence> loadStatesMap() {
        return combatStateRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CombatStatePersistence::getId,
                        Function.identity()
                ));
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<ElementMultiplierDomain> loadUserElements(
            Long userId,
            Map<Long, CombatElementPersistence> elementsMap
    ) {

        List<UserElementMultiplierPersistence> elementMultipliers =
                userElementMultiplierRepository.findByUserId(userId);

        return elementMultipliers.stream()
                .map(multiplier -> {

                    CombatElementPersistence element =
                            elementsMap.get(multiplier.getId().getElementId());

                    return ElementMultiplierMapper.toDomain(multiplier, element);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<ElementMultiplierDomain> loadEnemyElements(
            Long enemyId,
            Map<Long, CombatElementPersistence> elementsMap
    ) {

        List<EnemyElementMultiplierPersistence> elementMultipliers =
                enemyElementMultiplierRepository.findByEnemyId(enemyId);

        return elementMultipliers.stream()
                .map(multiplier -> {

                    CombatElementPersistence element =
                            elementsMap.get(multiplier.getId().getElementId());

                    return ElementMultiplierMapper.toDomain(multiplier, element);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<StatusResistanceDomain> loadUserResistances(
            Long userId,
            Map<Long, CombatStatePersistence> statesMap
    ) {

        List<UserStatusResistancePersistence> resistances =
                userStatusResistanceRepository.findByUserId(userId);

        return resistances.stream()
                .map(resistance -> {

                    CombatStatePersistence state =
                            statesMap.get(resistance.getId().getStateId());

                    return StatusResistanceMapper.toDomain(resistance, state);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<StatusResistanceDomain> loadEnemyResistances(
            Long enemyId,
            Map<Long, CombatStatePersistence> statesMap
    ) {

        List<EnemyStatusResistancePersistence> resistances =
                enemyStatusResistanceRepository.findByEnemyId(enemyId);

        return resistances.stream()
                .map(resistance -> {

                    CombatStatePersistence state =
                            statesMap.get(resistance.getId().getStateId());
                    return StatusResistanceMapper.toDomain(resistance, state);
                })
                .toList();
    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatActorDTO buildUserCombatActorDTO(Long userId, int currentHp, UserStatsPersistence userStats, Long combatId, Map<Long,
            CombatElementPersistence> elementsMap, Map<Long, CombatStatePersistence> statesMap){

        List<ElementMultiplierDTO> actorElements =
                loadUserElements(userId, elementsMap)
                        .stream()
                        .map(ElementMultiplierMapper::toDTO)
                        .toList();

        List<StatusResistanceDTO> actorResistances =
                loadUserResistances(userId, statesMap)
                        .stream()
                        .map(StatusResistanceMapper::toDTO)
                        .toList();

        List<CombatActiveStatusDTO> activeStatus = combatStateService.getActiveStatusDTO(combatId, CombatActorType.USER);

        StatsDTO statsDTO = StatsMapper.toDTO(userStats);

        return CombatActorMapper.toDTO(
                userId,
                currentHp,
                statsDTO,
                actorElements,
                actorResistances,
                activeStatus);

    }

    //------------------------------------------------------------------------------------------------------------------

    private CombatActorDTO buildEnemyCombatActorDTO(int currentHp, EnemyPersistence enemyPersistence, Long combatId, Map<Long,
            CombatElementPersistence> elementsMap, Map<Long, CombatStatePersistence> statesMap){

        List<ElementMultiplierDTO> actorElements =
                loadEnemyElements(enemyPersistence.getId(), elementsMap)
                        .stream()
                        .map(ElementMultiplierMapper::toDTO)
                        .toList();

        List<StatusResistanceDTO> actorResistances =
                loadEnemyResistances(enemyPersistence.getId(), statesMap)
                        .stream()
                        .map(StatusResistanceMapper::toDTO)
                        .toList();


        List<CombatActiveStatusDTO> activeStatus = combatStateService.getActiveStatusDTO(combatId, CombatActorType.ENEMY);

        StatsDTO statsDTO = StatsMapper.toDTO(enemyPersistence);

        return CombatActorMapper.toDTO(
                enemyPersistence.getId(),
                currentHp,
                statsDTO,
                actorElements,
                actorResistances,
                activeStatus);

    }

    //------------------------------------------------------------------------------------------------------------------

}