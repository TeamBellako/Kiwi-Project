package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/goals")
public class GoalController {
    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<List<GoalDTO>> createGoals(
            @RequestBody List<GoalDTO> goals,
            Authentication authentication) {
        List<GoalDTO> createdGoals = goalService.createGoals(goals, authentication);
        return ResponseEntity.status(201).body(createdGoals);
    }

    @PatchMapping("/{goalId}/update_progress")
    public ResponseEntity<GoalDTO> updateGoalProgress(
            @PathVariable Long goalId,
            Authentication authentication) {
        GoalDTO updatedGoal = goalService.updateGoalProgress(goalId, authentication);
        return ResponseEntity.ok(updatedGoal);
    }
    @PatchMapping("/{goalId}/update")
    public ResponseEntity<GoalDTO> updateGoal(
            @PathVariable Long goalId,
            @RequestBody GoalDTO goal,
            Authentication authentication) {
        GoalDTO updatedGoal = goalService.updateGoal(goalId, goal, authentication);
        return ResponseEntity.ok(updatedGoal);
    }

    @PatchMapping("/{goalId}/complete")
    public ResponseEntity<GoalDTO> completeGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        GoalDTO completedGoal = goalService.completeGoal(goalId, authentication);
        return ResponseEntity.ok(completedGoal);
    }

    @PatchMapping("/{goalId}/uncompleted")
    public ResponseEntity<GoalDTO> uncompleteGoal(
            @PathVariable Long goalId,
            Authentication authentication) {
        GoalDTO uncompletedGoal = goalService.uncompleteGoal(goalId, authentication);
        return ResponseEntity.ok(uncompletedGoal);
    }

    @GetMapping
    public ResponseEntity<List<GoalDTO>> getGoalsByDate(
            @RequestParam("date") String date,
            Authentication authentication) {
        List<GoalDTO> goals = goalService.getGoalsByDate(date, authentication);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/in_progress")
    public ResponseEntity<List<GoalDTO>> getInProgressGoals(Authentication authentication) {
        List<GoalDTO> goals = goalService.getGoalsInProgress(authentication);
        return ResponseEntity.ok().body(goals);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<GoalDTO>> getAllGoals(Authentication authentication) {
        List<GoalDTO> allGoals = goalService.getAllGoals(authentication);
        return ResponseEntity.ok(allGoals);
    }
}
