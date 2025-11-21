package com.kiwi.quests;

import com.kiwi.features.quests.controllers.*;
import com.kiwi.features.quests.data.*;
import com.kiwi.features.quests.exceptions.*;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static com.kiwi.quests.QuestTestFactory.*;

public class QuestServiceTests {

    private final InMemoryTestDatabase db = new InMemoryTestDatabase();

    private final QuestTestRepositoryInMemory questRepo = new QuestTestRepositoryInMemory(db);
    private final SubquestTestRepositoryInMemory subRepo = new SubquestTestRepositoryInMemory(db);
    private final UserQuestStatusTestRepositoryInMemory userQuestRepo = new UserQuestStatusTestRepositoryInMemory(db);
    private final UserSubquestStatusTestRepositoryInMemory userSubRepo = new UserSubquestStatusTestRepositoryInMemory(db);
    private final QuestProgressService progress = new QuestProgressService();

    private final QuestService service =
            new QuestService(questRepo, subRepo, userQuestRepo, userSubRepo, progress);

    private final int userId = 1;

    // ============================================================================================
    // GET QUESTS
    // ============================================================================================

    @Test
    public void getActiveQuestsForUser_returnsOnlyActive() {
        QuestPersistence q1 = questRepo.saveAndFlush(quest(1));
        QuestPersistence q2 = questRepo.saveAndFlush(quest(2));

        userQuestRepo.save(activeQuestStatus(userId, q1));
        userQuestRepo.save(completedQuestStatus(userId, q2));

        List<QuestDTO> result = service.getActiveQuestsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(q1.getId(), result.get(0).getQuestId());
    }

    @Test
    public void getCompletedQuestsForUser_returnsOnlyCompleted() {
        QuestPersistence q1 = questRepo.saveAndFlush(quest(1));
        QuestPersistence q2 = questRepo.saveAndFlush(quest(2));

        userQuestRepo.save(activeQuestStatus(userId, q1));
        userQuestRepo.save(completedQuestStatus(userId, q2));

        List<QuestDTO> result = service.getCompletedQuestsForUser(userId);

        assertEquals(1, result.size());
        assertEquals(q2.getId(), result.get(0).getQuestId());
    }

    // ============================================================================================
    // GIVE QUEST
    // ============================================================================================

    @Test
    public void giveQuestToUser_initializesQuestAndSubquests() {
        QuestPersistence questObj = questRepo.saveAndFlush(quest(10));

        SubquestPersistence s1 = subRepo.saveAndFlush(subquest(100, questObj, 1));
        SubquestPersistence s2 = subRepo.saveAndFlush(subquest(101, questObj, 2));

        QuestDTO dto = service.giveQuestToUser(userId, 10);

        assertEquals(questObj.getId(), dto.getQuestId());
        assertEquals(QuestStatus.ACTIVE.name(), dto.getStatus());
        assertEquals(2, dto.getSubquests().size());

        assertEquals(SubquestStatus.ACTIVE.name(), dto.getSubquests().get(0).getStatus());
        assertEquals(SubquestStatus.LOCKED.name(), dto.getSubquests().get(1).getStatus());
    }

    @Test(expected = QuestNotFoundException.class)
    public void giveQuestToUser_failsIfQuestDoesNotExist() {
        service.giveQuestToUser(userId, 999);
    }

    // ============================================================================================
    // COMPLETE / FAIL SUBQUEST
    // ============================================================================================

    @Test
    public void completeSubquest_unlocksNextOne() {
        QuestPersistence questObj = questRepo.saveAndFlush(quest(1));

        SubquestPersistence s1 = subRepo.saveAndFlush(subquest(10, questObj, 1));
        SubquestPersistence s2 = subRepo.saveAndFlush(subquest(11, questObj, 2));

        service.giveQuestToUser(userId, questObj.getId());

        SubquestResultDTO result = service.completeSubquest(userId, 10);

        assertEquals(SubquestStatus.COMPLETED.name(), result.getUpdatedSubquest().getStatus());
        assertNotNull(result.getNextSubquest());
        assertEquals(SubquestStatus.ACTIVE.name(), result.getNextSubquest().getStatus());
    }

    @Test
    public void completeSubquest_completesQuestWhenAllFinished() {
        QuestPersistence questObj = questRepo.saveAndFlush(quest(1));

        SubquestPersistence s1 = subRepo.saveAndFlush(subquest(10, questObj, 1));
        SubquestPersistence s2 = subRepo.saveAndFlush(subquest(11, questObj, 2));

        userQuestRepo.save(activeQuestStatus(userId, questObj));

        userSubRepo.save(completedSubquestStatus(userId, s1));
        userSubRepo.save(activeSubquestStatus(userId, s2));

        SubquestResultDTO result = service.completeSubquest(userId, s2.getId());

        assertNotNull(result.getCompletedQuest());
        assertEquals(QuestStatus.COMPLETED.name(), result.getCompletedQuest().getStatus());
    }

    @Test(expected = SubquestNotFoundException.class)
    public void completeSubquest_failsIfSubquestDoesNotExist() {
        service.completeSubquest(userId, 999);
    }

    @Test(expected = SubquestStatusNotFoundException.class)
    public void completeSubquest_failsIfUserHasNoStatus() {
        QuestPersistence questNotPersisted = quest(1);
        SubquestPersistence s1 = subRepo.saveAndFlush(subquest(10, questNotPersisted, 1));
        service.completeSubquest(userId, s1.getId());
    }

    @Test
    public void failSubquest_setsFailedStatus() {
        QuestPersistence questObj = questRepo.saveAndFlush(quest(1));

        SubquestPersistence s1 = subRepo.saveAndFlush(subquest(10, questObj, 1));
        SubquestPersistence s2 = subRepo.saveAndFlush(subquest(11, questObj, 2));

        service.giveQuestToUser(userId, questObj.getId());

        SubquestResultDTO result = service.failSubquest(userId, s1.getId());

        assertEquals(SubquestStatus.FAILED.name(), result.getUpdatedSubquest().getStatus());
    }
}
