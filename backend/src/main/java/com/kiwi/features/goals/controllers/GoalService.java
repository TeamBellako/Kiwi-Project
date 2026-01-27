package com.kiwi.features.goals.controllers;

import com.kiwi.features.goals.data.*;
import com.kiwi.features.goals.exceptions.GoalNotFoundException;
import com.kiwi.features.goals.exceptions.GoalUnauthorizedException;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
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
    private final UsersService usersService;

    public GoalService(GoalRepository goalRepository, UsersRepository usersRepository, UsersService usersService) {
        this.goalRepository = goalRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    private UsersPersistence getUserFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsersNotFoundException(email));
    }

    @Transactional
    public GoalDTO updateGoalProgress(Long goalId, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        // Verificar que el goal pertenece al usuario autenticado
        if (!existingGoal.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        if (existingGoal.getStatus() != GoalStatus.IN_PROGRESS) {
            throw new GoalUnauthorizedException("Only goals with IN_PROGRESS status can be updated");
        }

        int progression = (int)(existingGoal.getTarget() / 10);
        existingGoal.setValue(existingGoal.getValue() + progression);

        if (existingGoal.getValue() >= existingGoal.getTarget()) {
            return completeGoal(goalId, authentication);
        }

        GoalPersistence updatedGoal = goalRepository.save(existingGoal);

        return GoalDataMapper.toDTO(updatedGoal);
    }

    @Transactional
    public GoalDTO updateGoal(Long goalId, GoalDTO goal, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        // Verificar que el goal pertenece al usuario autenticado
        if (!existingGoal.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        // if (existingGoal.getStatus() != GoalStatus.IN_PROGRESS) {
            // throw new GoalUnauthorizedException("Only goals with IN_PROGRESS status can be updated");
        // }

        existingGoal.setValue(goal.getValue());
        if (existingGoal.getValue() < existingGoal.getTarget()) {
            existingGoal.setStatus(GoalStatus.IN_PROGRESS);
        } else {
            existingGoal.setStatus(GoalStatus.COMPLETED);
        }

        GoalPersistence updatedGoal = goalRepository.save(existingGoal);

        return GoalDataMapper.toDTO(updatedGoal);
    }

    @Transactional
    public List<GoalDTO> createGoals(List<GoalDTO> goals, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        // Si no se proporciona fecha en los goals, usar la fecha actual
        LocalDate defaultDate = LocalDate.now();
        
        // Si los goals tienen fecha, usar la primera (todas deberían tener la misma fecha)
        final LocalDate date;
        if (!goals.isEmpty() && goals.get(0).getDate() != null && !goals.get(0).getDate().isEmpty()) {
            date = LocalDate.parse(goals.get(0).getDate(), DATE_FORMATTER);
        } else {
            date = defaultDate;
        }

        // Eliminar goals existentes para esta fecha
        // List<GoalPersistence> existingGoals = goalRepository.findByUserAndDate(user, date);
        // goalRepository.deleteAll(existingGoals);

        List<GoalPersistence> newGoals = goals.stream()
                .map(dto -> {
                    dto.setId(null); // Forzar id a null para nuevas entidades
                    return GoalDataMapper.toEntity(dto, user, date);
                })
                .collect(Collectors.toList());

        List<GoalPersistence> savedGoals = goalRepository.saveAll(newGoals);

        return savedGoals.stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca un goal como completado y añade puntos al usuario.
     * SOLO se puede marccar como no completado un goal en estado IN_PROGRESS. 
     * SOLO puede completar el propietario del goal.
     */
    @Transactional
    public GoalDTO completeGoal(Long goalId, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        // Verificar que el goal pertenece al usuario autenticado
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to complete this goal");
        }

        // Verificar que el goal está en IN_PROGRESS
        if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
            return GoalDataMapper.toDTO(goal);
        }

        // Añadir puntos al usuario ANTES de cambiar el estado
        usersService.addPointsToUser(user.getId(), goal.getReward());

        // Cambiar estado y guardar
        goal.setStatus(GoalStatus.COMPLETED);
        goal.setValue(goal.getTarget());
        GoalPersistence savedGoal = goalRepository.save(goal);

        return GoalDataMapper.toDTO(savedGoal);
    }

    /**
     * Marca un goal como no completado.
     * SOLO se puede marccar como no completado un goal en estado IN_PROGRESS.
     * SOLO puedo marcarse como no completado el propietario del goal.
     */
    @Transactional
    public GoalDTO uncompleteGoal(Long goalId, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));


        // Verificar que el goal pertenece al usuario autenticado
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to uncomplete this goal");
        }

        // Verificar que el goal está en IN_PROGRESS
        if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
            return GoalDataMapper.toDTO(goal);
        }

        // Cambiar estado y guardar
        goal.setStatus(GoalStatus.NOT_COMPLETED);
        GoalPersistence savedGoal = goalRepository.saveAndFlush(goal);
        
        return GoalDataMapper.toDTO(savedGoal);
    }

    // region GETTERS
    public GoalDTO getGoalById(Long goalId, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        GoalPersistence goal = goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new GoalNotFoundException(goalId));

        return GoalDataMapper.toDTO(goal);
    }

    public List<GoalDTO> getGoalsByDate(String dateString, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

        return goalRepository
                .findByUserAndDateAndCategoryNot(user, date, GoalCategory.APP_USAGE)
                .stream()
                .map(GoalDataMapper::toDTO)
                .toList();
    }

    public List<GoalDTO> getAllGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return goalRepository.findByUserAndCategoryNotOrderByDateDesc(user, GoalCategory.APP_USAGE)
                .stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<GoalDTO> getGoalsInProgress(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate today = LocalDate.now();

        return goalRepository.findByUserAndStatusAndDateBeforeAndCategoryNotOrderByDateDesc(user,GoalStatus.IN_PROGRESS, today, GoalCategory.APP_USAGE)
                .stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<GoalDTO> getAppGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return goalRepository.findByUserAndCategory(user, GoalCategory.APP_USAGE)
                .stream()
                .map(GoalDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    // endregions
}
