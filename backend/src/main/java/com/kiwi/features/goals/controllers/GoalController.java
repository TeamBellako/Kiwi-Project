package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalDTO;
import com.kiwi.features.goals.data.UserGoalStatusDTO;
import com.kiwi.features.localization.LocalizedDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/goals")
public class GoalController {
    private final GoalService goalService;
    private final LocalizedDataService localizedDataService;

    @Autowired
    public GoalController(GoalService goalService, LocalizedDataService localizedDataService) {
        this.goalService = goalService;
        this.localizedDataService = localizedDataService;
    }

    @PostMapping
    public ResponseEntity<List<UserGoalStatusDTO>> createGoals(
            @RequestBody List<UserGoalStatusDTO> goals,
            Authentication authentication) {
        List<UserGoalStatusDTO> createdGoals = goalService.createGoals(goals, authentication);
            return ResponseEntity.status(201).body(localize(createdGoals));
    }

    @PatchMapping("/{goalId}/update_progress")
    public ResponseEntity<UserGoalStatusDTO> updateGoalProgress(
            @PathVariable Long goalId,
            Authentication authentication) {
        UserGoalStatusDTO updatedGoal = goalService.updateGoalProgress(goalId, authentication);
            return ResponseEntity.ok(localize(updatedGoal));
    }

    @PatchMapping("/{goalId}/update")
    public ResponseEntity<UserGoalStatusDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody UserGoalStatusDTO goal,
            Authentication authentication) {
        UserGoalStatusDTO updatedGoal = goalService.updateGoal(goalId, goal, authentication);
            return ResponseEntity.ok(localize(updatedGoal));
    }

    @PatchMapping("/{goalId}/complete")
    public ResponseEntity<UserGoalStatusDTO> completeGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        UserGoalStatusDTO completedGoal = goalService.completeGoal(goalId, authentication);
            return ResponseEntity.ok(localize(completedGoal));
    }

    @PatchMapping("/{goalId}/uncompleted")
    public ResponseEntity<UserGoalStatusDTO> uncompleteGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        UserGoalStatusDTO uncompletedGoal = goalService.uncompleteGoal(goalId, authentication);
            return ResponseEntity.ok(localize(uncompletedGoal));
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<UserGoalStatusDTO> getGoalById(
            @PathVariable Long goalId,
            Authentication authentication) {
        UserGoalStatusDTO goal = goalService.getGoalById(goalId, authentication);
            return ResponseEntity.ok(localize(goal));
    }

    @GetMapping("/app_usage")
    public ResponseEntity<List<UserGoalStatusDTO>> getAppGoals(
            Authentication authentication) {
        List<UserGoalStatusDTO> goals = goalService.getAppGoals(authentication);
            return ResponseEntity.ok(localize(goals));
    }

    @GetMapping("/skill")
    public ResponseEntity<List<UserGoalStatusDTO>> getSkillGoals(
            Authentication authentication) {
        List<UserGoalStatusDTO> goals = goalService.getSkillGoals(authentication);
            return ResponseEntity.ok(localize(goals));
    }

    @GetMapping
    public ResponseEntity<List<UserGoalStatusDTO>> getGoalsByDate(
            @RequestParam("date") String date,
            Authentication authentication) {
        List<UserGoalStatusDTO> goals = goalService.getGoalsByDate(date, authentication);
            return ResponseEntity.ok(localize(goals));
    }

    @GetMapping("/in_progress")
    public ResponseEntity<List<UserGoalStatusDTO>> getInProgressGoals(Authentication authentication) {
        List<UserGoalStatusDTO> goals = goalService.getGoalsInProgress(authentication);
        return ResponseEntity.ok().body(localize(goals));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserGoalStatusDTO>> getAllGoals(Authentication authentication) {
        List<UserGoalStatusDTO> allGoals = goalService.getAllGoals(authentication);
        return ResponseEntity.ok(localize(allGoals));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<GoalDTO>> getGoalDefinitions(Authentication authentication) {
        List<GoalDTO> definitions = goalService.getGoalDefinitions(authentication);
        return ResponseEntity.ok(definitions);
    }

    @PostMapping("/app_usage/auto_review")
    public ResponseEntity<List<UserGoalStatusDTO>> autoReviewAppUsageGoals(Authentication authentication) {
        List<UserGoalStatusDTO> reviewed = goalService.autoReviewAppUsageGoals(authentication);
        return ResponseEntity.ok(localize(reviewed));
    }

    private UserGoalStatusDTO localize(UserGoalStatusDTO goal) {
        return localize(List.of(goal)).get(0);
    }

    private List<UserGoalStatusDTO> localize(List<UserGoalStatusDTO> goals) {
        return localizedDataService.localizeGoalStatuses(goals);
    }
}

