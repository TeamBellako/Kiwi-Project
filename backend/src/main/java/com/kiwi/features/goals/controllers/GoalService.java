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

    private final UserGoalStatusRepository userGoalStatusRepository;
    private final GoalRepository goalRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public GoalService(
            UserGoalStatusRepository userGoalStatusRepository,
            GoalRepository goalRepository,
            UsersRepository usersRepository,
            UsersService usersService) {
        this.userGoalStatusRepository = userGoalStatusRepository;
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
    public UserGoalStatusDTO updateGoalProgress(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence existing = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        if (existing.getStatus() != GoalStatus.IN_PROGRESS) {
            throw new GoalUnauthorizedException("Only goals with IN_PROGRESS status can be updated");
        }

        Integer target = existing.getGoal().getTarget();
        Integer increment = Math.max(1, target / 10);
        Integer newValue = Math.min(existing.getValue() + increment, target);
        existing.setValue(newValue);

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(existing));
    }

    @Transactional
    public UserGoalStatusDTO updateGoal(Long id, UserGoalStatusDTO dto, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence existing = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to update this goal");
        }

        existing.setValue(dto.getValue());

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(existing));
    }

    @Transactional
    public List<UserGoalStatusDTO> createGoals(List<UserGoalStatusDTO> dtos, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        LocalDate defaultDate = LocalDate.now();
        final LocalDate date;
        if (!dtos.isEmpty() && dtos.get(0).getDate() != null && !dtos.get(0).getDate().isEmpty()) {
            date = LocalDate.parse(dtos.get(0).getDate(), DATE_FORMATTER);
        } else {
            date = defaultDate;
        }

        List<UserGoalStatusPersistence> newEntities = dtos.stream()
                .map(dto -> {
                    dto.setId(null);
                    GoalPersistence goal = goalRepository.findById(dto.getGoalId())
                            .orElseThrow(() -> new GoalNotFoundException(dto.getGoalId()));
                    return UserGoalStatusDataMapper.toEntity(dto, user, goal, date);
                })
                .collect(Collectors.toList());

        return userGoalStatusRepository.saveAll(newEntities).stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca un goal como completado y añade puntos al usuario.
     * SOLO se puede marcar como completado un goal en estado IN_PROGRESS.
     * SOLO puede completar el propietario del goal.
     */
    @Transactional
    public UserGoalStatusDTO completeGoal(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to complete this goal");
        }

        if (entry.getStatus() != GoalStatus.IN_PROGRESS) {
            return UserGoalStatusDataMapper.toDTO(entry);
        }

        usersService.addPointsToUser(user.getId(), entry.getGoal().getReward());

        entry.setStatus(GoalStatus.COMPLETED);
        entry.setValue(entry.getGoal().getTarget());

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.save(entry));
    }

    /**
     * Marca un goal como no completado.
     * SOLO se puede marcar como no completado un goal en estado IN_PROGRESS.
     * SOLO puede marcarlo el propietario del goal.
     */
    @Transactional
    public UserGoalStatusDTO uncompleteGoal(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new GoalUnauthorizedException("You are not authorized to uncomplete this goal");
        }

        if (entry.getStatus() != GoalStatus.IN_PROGRESS) {
            return UserGoalStatusDataMapper.toDTO(entry);
        }

        entry.setStatus(GoalStatus.NOT_COMPLETED);

        return UserGoalStatusDataMapper.toDTO(userGoalStatusRepository.saveAndFlush(entry));
    }

    // region GETTERS
    public UserGoalStatusDTO getGoalById(Long id, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        UserGoalStatusPersistence entry = userGoalStatusRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GoalNotFoundException(id));

        return UserGoalStatusDataMapper.toDTO(entry);
    }

    public List<UserGoalStatusDTO> getGoalsByDate(String dateString, Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);

        return userGoalStatusRepository
                .findByUserAndDateAndGoal_Category(user, date, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .toList();
    }

    public List<UserGoalStatusDTO> getAllGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return userGoalStatusRepository
                .findByUserAndGoal_CategoryOrderByDateDesc(user, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getGoalsInProgress(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);
        LocalDate today = LocalDate.now();

        return userGoalStatusRepository
                .findByUserAndStatusAndDateBeforeAndGoal_CategoryOrderByDateDesc(
                        user, GoalStatus.IN_PROGRESS, today, GoalCategory.DAILY_CHALLENGES)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getAppGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return userGoalStatusRepository
                .findByUserAndGoal_Category(user, GoalCategory.APP_USAGE)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserGoalStatusDTO> getSkillGoals(Authentication authentication) {
        UsersPersistence user = getUserFromAuthentication(authentication);

        return userGoalStatusRepository
                .findByUserAndGoal_Category(user, GoalCategory.SKILL)
                .stream()
                .map(UserGoalStatusDataMapper::toDTO)
                .collect(Collectors.toList());
    }
    // endregion
}
