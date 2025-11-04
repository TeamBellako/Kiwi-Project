package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.*;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoalService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    
    private final GoalRepository goalRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository, UsersRepository usersRepository) {
        this.goalRepository = goalRepository;
        this.usersRepository = usersRepository;
    }

    private UsersPersistence getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersNotFoundException(email));
    }

    @Transactional
    public GoalsListDTO createGoals(GoalsListDTO goalsListDTO, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate date = LocalDate.parse(goalsListDTO.getDate(), DATE_FORMATTER);

        // Eliminar goals existentes para esta fecha
        List<GoalPersistence> existingGoals = goalRepository.findByUserAndDate(user, date);
        goalRepository.deleteAll(existingGoals);

        // Crear nuevas goals
        List<GoalPersistence> newGoals = goalsListDTO.getGoals().stream()
                .map(dto -> GoalDataMapper.toEntity(dto, user, date))
                .collect(Collectors.toList());

        List<GoalPersistence> savedGoals = goalRepository.saveAll(newGoals);

        return GoalDataMapper.toGoalsListDTO(date, savedGoals);
    }

    @Transactional
    public GoalDTO updateGoal(GoalDTO goalDTO, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence goal = goalRepository.findByIdAndUser(goalDTO.getId(), user)
                .orElseThrow(() -> new GoalNotFoundException(goalDTO.getId()));

        // Actualizar campos
        goal.setObjective(goalDTO.getObjective());
        goal.setCategory(GoalCategory.valueOf(goalDTO.getCategory()));
        goal.setStatus(GoalStatus.valueOf(goalDTO.getStatus()));
        goal.setPoints(goalDTO.getPoints());

        GoalPersistence updatedGoal = goalRepository.save(goal);

        return GoalDataMapper.toDTO(updatedGoal);
    }

    @Transactional(readOnly = true)
    public GoalsListDTO getGoalsByDate(String dateString, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

        List<GoalPersistence> goals = goalRepository.findByUserAndDate(user, date);

        return GoalDataMapper.toGoalsListDTO(date, goals);
    }

    @Transactional(readOnly = true)
    public List<GoalsListDTO> getAllGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        List<GoalPersistence> allGoals = goalRepository.findByUserOrderByDateDesc(user);

        // Agrupar por fecha
        Map<LocalDate, List<GoalPersistence>> goalsByDate = allGoals.stream()
                .collect(Collectors.groupingBy(GoalPersistence::getDate));

        // Convertir a lista de GoalsListDTO
        return goalsByDate.entrySet().stream()
                .map(entry -> GoalDataMapper.toGoalsListDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // Ordenar descendente por fecha
                .collect(Collectors.toList());
    }
}
