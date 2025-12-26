package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SuggestedGoalsService {

    private final SuggestedGoalRepository suggestedGoalRepository;

    public SuggestedGoalsService(SuggestedGoalRepository suggestedGoalRepository) {
        this.suggestedGoalRepository = suggestedGoalRepository;
    }

    /**
     * Obtiene una lista aleatoria de hasta 4 goals sugeridos de la base de datos.
     * Estos son plantillas/objetivos por defecto que se pueden sugerir a los usuarios.
     */
    @Transactional(readOnly = true)
    public List<SuggestedGoalDTO> getSuggestedGoals(Authentication authentication) {
        List<SuggestedGoalPersistence> allSuggestedGoals = suggestedGoalRepository.findAll();

        if (allSuggestedGoals.isEmpty()) {
            return Collections.emptyList();
        }

        int sampleSize = Math.min(2, allSuggestedGoals.size());
        Collections.shuffle(allSuggestedGoals);
        List<SuggestedGoalPersistence> randomGoals = new ArrayList<>(allSuggestedGoals.subList(0, sampleSize));

        return SuggestedGoalDataMapper.toListNewGoalDTO(randomGoals);
    }
}
