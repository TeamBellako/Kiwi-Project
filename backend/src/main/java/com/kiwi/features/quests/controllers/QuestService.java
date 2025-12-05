package com.kiwi.features.quests.controllers;

import com.kiwi.features.quests.data.*;
import com.kiwi.features.quests.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final SubquestRepository subquestRepository;
    private final UserQuestStatusRepository userQuestStatusRepository;
    private final UserSubquestStatusRepository userSubquestStatusRepository;
    private final QuestProgressService progress;

    @Autowired
    public QuestService(
            QuestRepository questRepository,
            SubquestRepository subquestRepository,
            UserQuestStatusRepository userQuestStatusRepository,
            UserSubquestStatusRepository userSubquestStatusRepository,
            QuestProgressService progress
    ) {
        this.questRepository = questRepository;
        this.subquestRepository = subquestRepository;
        this.userQuestStatusRepository = userQuestStatusRepository;
        this.userSubquestStatusRepository = userSubquestStatusRepository;
        this.progress = progress;
    }

    // ============================================================================================
    // GET USER QUESTS
    // ============================================================================================

    public List<QuestDTO> getActiveQuestsForUser(Long userId) {
        return getQuestsForUserByStatus(userId, QuestStatus.ACTIVE);
    }

    public List<QuestDTO> getCompletedQuestsForUser(Long userId) {
        return getQuestsForUserByStatus(userId, QuestStatus.COMPLETED);
    }

    private List<QuestDTO> getQuestsForUserByStatus(Long userId, QuestStatus status) {

        // cargar estados de quest SOLO para este usuario
        List<UserQuestStatusPersistence> userQuests =
                userQuestStatusRepository.findByIdUserId(userId);

        return userQuests.stream()
                .filter(qs -> qs.getStatus() == status)
                .map(qs -> {
                    QuestPersistence quest = qs.getQuest();

                    // cargar subquests de la quest y sus estados en lote
                    List<SubquestPersistence> subquests =
                            subquestRepository.findAllByQuestIdOrderByOrderIndex(quest.getId());

                    List<UserSubquestStatusPersistence> userSubs =
                            userSubquestStatusRepository.findByUserIdAndQuestIdOrdered(
                                    userId,
                                    quest.getId()
                            );

                    List<SubquestDomain> subDomains =
                            SubquestMapper.toDomainList(subquests, userSubs);

                    QuestDomain domain =
                            QuestDomainFactory.create(quest, qs, subDomains);

                    return QuestMapper.toDTO(domain);
                })
                .toList();
    }

    // ============================================================================================
    // GIVE QUEST
    // ============================================================================================

    @Transactional
    public QuestDTO giveQuestToUser(Long userId, int questId) {

        QuestPersistence quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestNotFoundException(questId));

        // 1) Crear dominio sin estado
        QuestDomain domain = new QuestDomain(
                quest.getId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                null,
                List.of()
        );

        // 2) Activar quest
        QuestDomain activated = progress.activateQuest(domain);

        // 3) Guardar estado de quest
        UserQuestStatusPersistence persistence =
                QuestMapper.toPersistence(userId, activated);

        userQuestStatusRepository.save(persistence);

        // 4) Inicializar subquests
        initializeSubquests(userId, questId);

        // 5) Recargar subquests + estados
        List<SubquestPersistence> subquests =
                subquestRepository.findAllByQuestIdOrderByOrderIndex(questId);

        List<UserSubquestStatusPersistence> userSubs =
                userSubquestStatusRepository.findByUserIdAndQuestIdOrdered(
                        userId,
                        (int) questId
                );

        List<SubquestDomain> subDomains =
                SubquestMapper.toDomainList(subquests, userSubs);

        // 6) Montar quest completa
        QuestDomain full = new QuestDomain(
                quest.getId(),
                quest.getName(),
                quest.getDescription(),
                quest.getExperience(),
                activated.getStatus(),
                subDomains
        );

        return QuestMapper.toDTO(full);
    }

    // ============================================================================================
    // INITIALIZE SUBQUESTS
    // ============================================================================================

    @Transactional
    public void initializeSubquests(Long userId, int questId) {

        List<SubquestPersistence> subquests =
                subquestRepository.findAllByQuestIdOrderByOrderIndex(questId);

        for (int i = 0; i < subquests.size(); i++) {

            SubquestPersistence subquest = subquests.get(i);

            UserSubquestStatusPersistence subquestStatus = new UserSubquestStatusPersistence();
            subquestStatus.setId(new UserSubquestStatusKey(userId, subquest.getId()));

            // primera subquest = ACTIVE, resto LOCKED
            subquestStatus.setStatus(i == 0 ? SubquestStatus.ACTIVE : SubquestStatus.LOCKED);
            subquestStatus.setSubquest(subquest);

            userSubquestStatusRepository.save(subquestStatus);
        }
    }

    // ============================================================================================
    // COMPLETE / FAIL SUBQUEST
    // ============================================================================================

    @Transactional
    public SubquestResultDTO completeSubquest(Long userId, int subquestId) {
        return processSubquestUpdate(userId, subquestId, false);
    }

    @Transactional
    public SubquestResultDTO failSubquest(Long userId, int subquestId) {
        return processSubquestUpdate(userId, subquestId, true);
    }

    @Transactional
    private SubquestResultDTO processSubquestUpdate(Long userId, int subquestId, boolean isFail) {

        SubquestPersistence subquest = subquestRepository.findById(subquestId)
                .orElseThrow(() -> new SubquestNotFoundException(subquestId));

        UserSubquestStatusPersistence current =
                userSubquestStatusRepository.findByIdUserIdAndIdSubquestId(userId, subquestId)
                        .orElseThrow(() -> new SubquestStatusNotFoundException(subquestId));

        // --- Convertir a dominio ---
        SubquestDomain domain = SubquestMapper.toDomain(subquest, current);

        SubquestDomain updated =
                isFail ? progress.failSubquest(domain)
                        : progress.completeSubquest(domain);

        // --- Guardar nuevo estado ---
        UserSubquestStatusPersistence persistence =
                SubquestMapper.toPersistence(userId, updated, subquestRepository);

        userSubquestStatusRepository.save(persistence);

        SubquestDTO updatedDTO = SubquestMapper.toDTO(updated);

        // --- Activar siguiente subquest ---
        SubquestDTO nextSubquestDTO = null;

        List<SubquestPersistence> questSubs =
                subquestRepository.findAllByQuestIdOrderByOrderIndex(subquest.getQuest().getId());

        int index = -1;
        for (int i = 0; i < questSubs.size(); i++) {
            if (questSubs.get(i).getId() == subquestId) {
                index = i;
                break;
            }
        }

        if (index != -1 && index + 1 < questSubs.size()) {
            SubquestPersistence next = questSubs.get(index + 1);

            UserSubquestStatusPersistence nextStatus =
                    userSubquestStatusRepository.findByIdUserIdAndIdSubquestId(userId, next.getId())
                            .orElseThrow();

            if (nextStatus.getStatus() == SubquestStatus.LOCKED) {
                SubquestDomain nextDomain = SubquestMapper.toDomain(next, nextStatus);
                SubquestDomain unlocked = progress.unlockSubquest(nextDomain);

                UserSubquestStatusPersistence saved =
                        userSubquestStatusRepository.save(SubquestMapper.toPersistence(userId, unlocked, subquestRepository));

                nextSubquestDTO = SubquestMapper.toDTO(unlocked);
            }
        }

        // --- Completar quest si no quedan subquests ---
        QuestDTO completedQuestDTO = null;

        List<UserSubquestStatusPersistence> allStatuses =
                userSubquestStatusRepository.findByUserIdAndQuestIdOrdered(userId, subquest.getQuest().getId());

        boolean allDone = allStatuses.stream()
                .allMatch(s -> s.getStatus() == SubquestStatus.COMPLETED
                        || s.getStatus() == SubquestStatus.FAILED);

        if (allDone) {

            UserQuestStatusPersistence questStatus =
                    userQuestStatusRepository
                            .findByIdUserIdAndIdQuestId(userId, subquest.getQuest().getId())
                            .orElseThrow();

            QuestDomain questDomain =
                    QuestMapper.toDomain(subquest.getQuest(), questStatus, List.of());

            QuestDomain completedQuest = progress.completeQuest(questDomain);

            userQuestStatusRepository.save(QuestMapper.toPersistence(userId, completedQuest));

            completedQuestDTO = QuestMapper.toDTO(completedQuest);
        }

        // --- Respuesta final ---
        return SubquestResultDTO.builder()
                .updatedSubquest(updatedDTO)
                .nextSubquest(nextSubquestDTO)
                .completedQuest(completedQuestDTO)
                .build();
    }

}
