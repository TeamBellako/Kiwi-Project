package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.GoalDTO;
import com.kiwi.features.goals.data.GoalsListDTO;

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
    public ResponseEntity<GoalsListDTO> createGoals(
            @RequestBody GoalsListDTO goalsListDTO,
            Authentication authentication) {
        GoalsListDTO createdGoals = goalService.createGoals(goalsListDTO, authentication);
        return ResponseEntity.status(201).body(createdGoals);
    }

    @PatchMapping("/{goalId}/complete")
    public ResponseEntity<GoalDTO> completeGoal(
            @PathVariable String goalId,
            Authentication authentication) {
        GoalDTO completedGoal = goalService.completeGoal(goalId, authentication);
        return ResponseEntity.ok(completedGoal);
    }

    @PatchMapping("/{goalId}/uncompleted")
    public ResponseEntity<GoalDTO> uncompleteGoal(
            @PathVariable String goalId,
            Authentication authentication) {
        GoalDTO uncompletedGoal = goalService.uncompleteGoal(goalId, authentication);
        return ResponseEntity.ok(uncompletedGoal);
    }

    @GetMapping
    public ResponseEntity<GoalsListDTO> getGoalsByDate(
            @RequestParam("date") String date,
            Authentication authentication) {
        GoalsListDTO goals = goalService.getGoalsByDate(date, authentication);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/in_progress")
    public ResponseEntity<List<GoalsListDTO>> getInProgressGoals(Authentication authentication) {
        List<GoalsListDTO> goals = goalService.getGoalsInProgress(authentication);
        return ResponseEntity.ok().body(goals);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<GoalsListDTO>> getAllGoals(Authentication authentication) {
        List<GoalsListDTO> allGoals = goalService.getAllGoals(authentication);
        return ResponseEntity.ok(allGoals);
    }
}
