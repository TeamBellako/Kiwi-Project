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

        return userQuestStatusRepository.findByIdUserId(userId).stream()
                .filter(qs -> qs.getStatus() == status)
                .map(qs -> buildQuestDomain(userId, qs.getQuest().getId()))
                .map(QuestMapper::toDTO)
                .toList();
    }

    // ============================================================================================
    // GIVE QUEST
    // ============================================================================================

    @Transactional
    public QuestDTO giveQuestToUser(Long userId, int questId) {

        QuestPersistence quest = questRepository.findById(questId)
                .orElseThrow(() -> new QuestNotFoundException(questId));

        UserQuestStatusPersistence questStatus =
                userQuestStatusRepository
                        .findByIdUserIdAndIdQuestId(userId, questId)
                        .orElse(null);

        QuestDomain activated = progress.activateQuest(QuestMapper.toDomain(quest,questStatus,List.of()));

        userQuestStatusRepository.saveAndFlush(QuestMapper.toPersistence(userId, activated.getStatus(),quest)
        );

        initializeSubquests(userId, questId);

        return QuestMapper.toDTO(buildQuestDomain(userId, questId)
        );
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

            UserSubquestStatusPersistence status = new UserSubquestStatusPersistence();
            status.setId(new UserSubquestStatusKey(userId, subquest.getId()));
            status.setStatus(i == 0 ? SubquestStatus.ACTIVE : SubquestStatus.LOCKED);
            status.setSubquest(subquest);

            userSubquestStatusRepository.saveAndFlush(status);
        }
    }

    // ============================================================================================
    // COMPLETE / FAIL SUBQUEST
    // ============================================================================================

    @Transactional
    public QuestDTO completeSubquest(Long userId, int subquestId) {
        return processSubquestUpdate(userId, subquestId, false);
    }

    @Transactional
    public QuestDTO failSubquest(Long userId, int subquestId) {
        return processSubquestUpdate(userId, subquestId, true);
    }

    @Transactional
    private QuestDTO processSubquestUpdate(Long userId, int subquestId, boolean isFail) {

        SubquestPersistence subquest = subquestRepository.findById(subquestId)
                .orElseThrow(() -> new SubquestNotFoundException(subquestId));

        UserSubquestStatusPersistence subquestStatus = userSubquestStatusRepository
                .findByIdUserIdAndIdSubquestId(userId, subquestId)
                .orElseThrow(() -> new SubquestStatusNotFoundException(subquestId));

        updateCurrentSubquest(userId, subquest, subquestStatus, isFail);
        unlockNextSubquestIfNeeded(userId, subquest);

        QuestDomain questDomain = buildQuestDomain(userId, subquest.getQuest().getId());

        if (hasAllSubquestCompleted(questDomain)) {
            QuestDomain completedQuestDomain = progress.completeQuest(questDomain);
            userQuestStatusRepository.saveAndFlush(
                    QuestMapper.toPersistence(userId, completedQuestDomain.getStatus(),subquest.getQuest())
            );
            questDomain = completedQuestDomain;
        }

        return QuestMapper.toDTO(questDomain);
    }

    // ============================================================================================
    // AUXILIARY METHODS
    // ============================================================================================

    private void updateCurrentSubquest(
            Long userId,
            SubquestPersistence subquestPersistence,
            UserSubquestStatusPersistence currentStatusPersistance,
            boolean isFail
    ) {

        SubquestDomain updatedSubquestDomain =
                isFail
                        ? progress.failSubquest(SubquestMapper.toDomain(subquestPersistence, currentStatusPersistance))
                        : progress.completeSubquest(SubquestMapper.toDomain(subquestPersistence, currentStatusPersistance));

        userSubquestStatusRepository.saveAndFlush(
                SubquestMapper.toPersistence(userId, updatedSubquestDomain.getStatus(), subquestPersistence)
        );
    }

    private void unlockNextSubquestIfNeeded(Long userId, SubquestPersistence current) {

        List<SubquestPersistence> subquests =
                subquestRepository.findAllByQuestIdOrderByOrderIndex(
                        current.getQuest().getId()
                );

        int index = subquests.indexOf(current);
        if (index == -1 || index + 1 >= subquests.size())
        {
            return;
        }

        SubquestPersistence nextSubquest = subquests.get(index + 1);

        UserSubquestStatusPersistence nextSubquestStatus =
                userSubquestStatusRepository
                        .findByIdUserIdAndIdSubquestId(userId, nextSubquest.getId())
                        .orElseThrow();

        if (nextSubquestStatus.getStatus() == SubquestStatus.LOCKED)
        {
            SubquestDomain unlockedSubquestDomain =
                    progress.unlockSubquest(
                            SubquestMapper.toDomain(nextSubquest, nextSubquestStatus)
                    );

            userSubquestStatusRepository.saveAndFlush(
                    SubquestMapper.toPersistence(userId, unlockedSubquestDomain.getStatus(), nextSubquest)
            );
        }
    }

    private QuestDomain buildQuestDomain(Long userId, int questId) {

        UserQuestStatusPersistence questStatus =
                userQuestStatusRepository
                        .findByIdUserIdAndIdQuestId(userId, questId)
                        .orElseThrow();

        List<SubquestPersistence> subquests =
                subquestRepository.findAllByQuestIdOrderByOrderIndex(questId);

        List<UserSubquestStatusPersistence> subquestsStatuses =
                userSubquestStatusRepository.findByUserIdAndQuestIdOrdered(userId, questId);

        return QuestMapper.toDomain(
                questStatus.getQuest(),
                questStatus,
                SubquestMapper.toDomainList(subquests, subquestsStatuses)
        );
    }

    private boolean hasAllSubquestCompleted(QuestDomain quest) {
        return quest.getSubquests().stream()
                .allMatch(s ->
                        s.getStatus() == SubquestStatus.COMPLETED
                                || s.getStatus() == SubquestStatus.FAILED
                );
    }
}