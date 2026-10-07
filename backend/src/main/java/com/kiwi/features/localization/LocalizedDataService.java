package com.kiwi.features.localization;

import com.kiwi.features.goals.controllers.GoalRepository;
import com.kiwi.features.goals.data.UserGoalStatusDTO;
import com.kiwi.features.quests.controllers.QuestRepository;
import com.kiwi.features.quests.data.QuestDTO;
import com.kiwi.features.skills.controllers.SkillRepository;
import com.kiwi.features.skills.data.DTO.SkillDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Sobrescribe los textos de los DTO con los del idioma del usuario (lo resuelve el QueryRewriter). */
@Service
public class LocalizedDataService {

    private final QuestRepository questRepository;
    private final SkillRepository skillRepository;
    private final GoalRepository goalRepository;

    public LocalizedDataService(
            QuestRepository questRepository,
            SkillRepository skillRepository,
            GoalRepository goalRepository) {
        this.questRepository = questRepository;
        this.skillRepository = skillRepository;
        this.goalRepository = goalRepository;
    }

    public List<QuestDTO> localizeQuests(List<QuestDTO> quests) {
        if (quests == null || quests.isEmpty()) {
            return quests;
        }

        List<Integer> ids = quests.stream().map(QuestDTO::getQuestId).distinct().toList();
        Map<Integer, QuestRepository.LocalizedQuest> byId = questRepository.findLocalizedByIds(ids).stream()
                .collect(Collectors.toMap(QuestRepository.LocalizedQuest::getId, Function.identity()));

        quests.forEach(quest -> {
            QuestRepository.LocalizedQuest translation = byId.get(quest.getQuestId());
            if (translation != null) {
                quest.setName(translation.getName());
                quest.setDescription(translation.getDescription());
            }
        });
        return quests;
    }

    public List<SkillDTO> localizeSkills(List<SkillDTO> skills) {
        if (skills == null || skills.isEmpty()) {
            return skills;
        }

        List<Long> ids = skills.stream().map(SkillDTO::getSkillId).distinct().toList();
        Map<Long, SkillRepository.LocalizedSkill> byId = skillRepository.findLocalizedByIds(ids).stream()
                .collect(Collectors.toMap(SkillRepository.LocalizedSkill::getId, Function.identity()));

        skills.forEach(skill -> {
            SkillRepository.LocalizedSkill translation = byId.get(skill.getSkillId());
            if (translation != null) {
                skill.setName(translation.getName());
                skill.setDescription(translation.getDescription());
                skill.setQuote(translation.getQuote());
                skill.setCooldownOtherDescription(translation.getCooldownOtherDescription());
            }
        });
        return skills;
    }

    public List<UserGoalStatusDTO> localizeGoalStatuses(List<UserGoalStatusDTO> goals) {
        if (goals == null || goals.isEmpty()) {
            return goals;
        }

        List<Long> ids = goals.stream()
                .map(UserGoalStatusDTO::getGoalId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return goals;
        }

        Map<Long, GoalRepository.LocalizedGoal> byId = goalRepository.findLocalizedByIds(ids).stream()
                .collect(Collectors.toMap(GoalRepository.LocalizedGoal::getId, Function.identity()));

        goals.forEach(goal -> {
            GoalRepository.LocalizedGoal translation = byId.get(goal.getGoalId());
            if (translation != null) {
                goal.setName(translation.getName());
                goal.setAction(translation.getAction());
            }
        });
        return goals;
    }
}
