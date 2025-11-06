package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.*;
import com.kiwi.features.users.exceptions.CreateUserConflictException;
import com.kiwi.common.types.Email;
import com.kiwi.features.users.exceptions.CreateUserInvalidException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.common.utils.FormatUtils.formatDate;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(@Valid @NotNull LoginDTO loginDTO) {
        UsersDomain userDomain;
        try {
            userDomain = UsersDataMapper.toDomain(new UsersDTO(loginDTO.getEmail(), loginDTO.getPassword(), formatDate(LocalDate.now())));
        } catch (IllegalArgumentException e) {
            throw new CreateUserInvalidException(e.getMessage());
        }

        String email = userDomain.getEmail().value();
        if (usersRepository.existsByEmail(email)) {
            throw new CreateUserConflictException(email);
        }

        String hashedPassword = passwordEncoder.encode(userDomain.getPassword().value());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistence(userDomain, hashedPassword);
        usersRepository.saveAndFlush(usersPersistence);
    }

    public Optional<UsersPersistence> getUserByEmail(@NotNull Email email) {
        return usersRepository.findByEmail(email.value());
    }

    public Optional<UserPointsDTO> getUserPoints(@NotNull Email email) {
        return usersRepository.findByEmail(email.value())
                .map(UsersDataMapper::toPointsDTO);
    }

    public Optional<UserPointsDTO> getUserPointsById(@NotNull Long userId) {
        return usersRepository.findById(userId)
                .map(UsersDataMapper::toPointsDTO);
    }

    // Métodos del backend para gestionar puntos - solo para uso interno
    @Transactional
    public void addPointsToUser(@NotNull Long userId, @NotNull Integer pointsToAdd) {
        if (pointsToAdd <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }
        
        UsersPersistence user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        user.setCurrentPoints(user.getCurrentPoints() + pointsToAdd);
        user.setTotalPoints(user.getTotalPoints() + pointsToAdd);
        usersRepository.saveAndFlush(user);
    }

    @Transactional
    public void subtractPointsToUser(@NotNull Long userId, @NotNull Integer pointsToSubtract) {
        if (pointsToSubtract <= 0) {
            throw new IllegalArgumentException("Points to subtract must be positive");
        }
        
        UsersPersistence user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        int newCurrentPoints = Math.max(0, user.getCurrentPoints() - pointsToSubtract);
        user.setCurrentPoints(newCurrentPoints);
        usersRepository.saveAndFlush(user);
    }
}
