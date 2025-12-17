package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.SuggestedGoalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/goals/suggestions")
public class SuggestedGoalController {
    private final SuggestedGoalsService suggestedGoalsService;

    @Autowired
    public SuggestedGoalController(SuggestedGoalsService suggestedGoalsService) {
        this.suggestedGoalsService = suggestedGoalsService;
    }

    @GetMapping
    public ResponseEntity<List<SuggestedGoalDTO>> getSuggestedGoals(Authentication authentication) {
        List<SuggestedGoalDTO> suggestedGoals = suggestedGoalsService.getSuggestedGoals(authentication);
        return ResponseEntity.ok(suggestedGoals);
    }
}
