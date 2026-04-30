package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalPersistence;
import com.kiwi.features.goals.data.GoalStatus;
import com.kiwi.features.goals.data.UserGoalStatusPersistence;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.skills.events.SkillGivenEvent;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersPersistence;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
public class SkillGivenGoalListener {

    private final UsersRepository usersRepository;
    private final GoalRepository goalRepository;
    private final UserGoalStatusRepository userGoalStatusRepository;

    public SkillGivenGoalListener(
            UsersRepository usersRepository,
            GoalRepository goalRepository,
            UserGoalStatusRepository userGoalStatusRepository) {
        this.usersRepository = usersRepository;
        this.goalRepository = goalRepository;
        this.userGoalStatusRepository = userGoalStatusRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onSkillGiven(SkillGivenEvent event) {
        Long cooldownGoalId = event.cooldownGoalId();
        if (cooldownGoalId == null) {
            return;
        }

        UsersPersistence user = usersRepository.findById(event.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + event.userId()));

        GoalPersistence goal = goalRepository.findById(cooldownGoalId)
                .orElseThrow(() -> new GoalNotFoundException(cooldownGoalId));

        UserGoalStatusPersistence entry = UserGoalStatusPersistence.builder()
                .user(user)
                .goal(goal)
                .status(GoalStatus.IN_PROGRESS)
                .date(LocalDate.now())
                .value(0)
                .build();

        userGoalStatusRepository.save(entry);
    }
}
