package com.kiwi.localization;

import com.kiwi.features.goals.controllers.GoalRepository;
import com.kiwi.features.goals.data.UserGoalStatusDTO;
import com.kiwi.features.localization.LocalizedDataService;
import com.kiwi.features.quests.controllers.QuestRepository;
import com.kiwi.features.quests.controllers.SubquestRepository;
import com.kiwi.features.quests.data.QuestDTO;
import com.kiwi.features.quests.data.SubquestDTO;
import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.data.DTO.SkillDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalizedDataServiceTests {

    @Mock private QuestRepository questRepository;
    @Mock private SubquestRepository subquestRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private GoalRepository goalRepository;

    private LocalizedDataService localizedDataService;

    @Before
    public void setUp() {
        localizedDataService = new LocalizedDataService(
            questRepository, subquestRepository, skillRepository, goalRepository);
    }

    @Test
    public void localizeQuests_overridesNameAndDescription() {
        QuestDTO quest = QuestDTO.builder().questId(7).build();
        QuestRepository.LocalizedQuest translation = mock(QuestRepository.LocalizedQuest.class);
        when(translation.getId()).thenReturn(7);
        when(translation.getName()).thenReturn("Nombre traducido");
        when(translation.getDescription()).thenReturn("Descripcion traducida");
        when(questRepository.findLocalizedByIds(List.of(7))).thenReturn(List.of(translation));

        localizedDataService.localizeQuests(List.of(quest));

        assertEquals("Nombre traducido", quest.getName());
        assertEquals("Descripcion traducida", quest.getDescription());
    }

    @Test
    public void localizeQuests_overridesNestedSubquestName() {
        SubquestDTO subquest = SubquestDTO.builder().subquestId(12).name("English name").build();
        QuestDTO quest = QuestDTO.builder().questId(7).subquests(List.of(subquest)).build();
        QuestRepository.LocalizedQuest questTranslation = mock(QuestRepository.LocalizedQuest.class);
        when(questTranslation.getId()).thenReturn(7);
        when(questRepository.findLocalizedByIds(List.of(7))).thenReturn(List.of(questTranslation));

        SubquestRepository.LocalizedSubquest translation = mock(SubquestRepository.LocalizedSubquest.class);
        when(translation.getId()).thenReturn(12);
        when(translation.getName()).thenReturn("Nombre de subquest traducido");
        when(subquestRepository.findLocalizedByIds(List.of(12))).thenReturn(List.of(translation));

        localizedDataService.localizeQuests(List.of(quest));

        assertEquals("Nombre de subquest traducido", subquest.getName());
    }

    @Test
    public void localizeSubquest_overridesName() {
        SubquestDTO subquest = SubquestDTO.builder().subquestId(12).name("English name").build();
        SubquestRepository.LocalizedSubquest translation = mock(SubquestRepository.LocalizedSubquest.class);
        when(translation.getId()).thenReturn(12);
        when(translation.getName()).thenReturn("Nombre de subquest traducido");
        when(subquestRepository.findLocalizedByIds(List.of(12))).thenReturn(List.of(translation));

        localizedDataService.localizeSubquest(subquest);

        assertEquals("Nombre de subquest traducido", subquest.getName());
    }

    @Test
    public void localizeSkills_overridesLocalizedTexts() {
        SkillDTO skill = SkillDTO.builder().skillId(12L).build();
        SkillRepository.LocalizedSkill translation = mock(SkillRepository.LocalizedSkill.class);
        when(translation.getId()).thenReturn(12L);
        when(translation.getName()).thenReturn("Nombre traducido");
        when(translation.getDescription()).thenReturn("Descripcion traducida");
        when(translation.getQuote()).thenReturn("Cita traducida");
        when(translation.getCooldownOtherDescription()).thenReturn("Espera traducida");
        when(skillRepository.findLocalizedByIds(List.of(12L))).thenReturn(List.of(translation));

        localizedDataService.localizeSkills(List.of(skill));

        assertEquals("Nombre traducido", skill.getName());
        assertEquals("Descripcion traducida", skill.getDescription());
        assertEquals("Cita traducida", skill.getQuote());
        assertEquals("Espera traducida", skill.getCooldownOtherDescription());
    }

    @Test
    public void localizeGoalStatuses_overridesNameAndAction() {
        UserGoalStatusDTO status = UserGoalStatusDTO.builder().goalId(18L).build();
        GoalRepository.LocalizedGoal translation = mock(GoalRepository.LocalizedGoal.class);
        when(translation.getId()).thenReturn(18L);
        when(translation.getName()).thenReturn("Generic name");
        when(translation.getAction()).thenReturn("Accion traducida");
        when(goalRepository.findLocalizedByIds(List.of(18L))).thenReturn(List.of(translation));

        localizedDataService.localizeGoalStatuses(List.of(status));

        assertEquals("Generic name", status.getName());
        assertEquals("Accion traducida", status.getAction());
    }
}
