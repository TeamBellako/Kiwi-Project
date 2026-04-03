package com.kiwi.features.users.controllers;

import com.kiwi.features.nodes.events.NodeUnlockedEvent;
import com.kiwi.features.users.events.UserCreatedEvent;
import com.kiwi.common.types.Password;
import com.kiwi.common.types.PositiveOrZeroInteger;
import com.kiwi.features.users.data.*;
import com.kiwi.features.users.exceptions.CreateUserConflictException;
import com.kiwi.common.types.Email;
import com.kiwi.features.users.exceptions.CreateUserInvalidException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.util.Optional;

import static com.kiwi.common.utils.FormatUtils.formatDate;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void createUser(@Valid @NotNull LoginDTO loginDTO) {

        String email = loginDTO.getEmail();
        if (usersRepository.existsByEmail(email)) {
            throw new CreateUserConflictException(email);
        }
        UsersDTO userDto = new UsersDTO(email, formatDate(LocalDate.now()));

        Password validPassword;
        try {
            validPassword = new Password(loginDTO.getPassword());
        } catch (IllegalArgumentException e) {
            throw new CreateUserInvalidException(e.getMessage());
        }

        String hashedPassword = passwordEncoder.encode(validPassword.value());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistenceWithoutPoints(userDto, hashedPassword);
        usersRepository.saveAndFlush(usersPersistence);

        eventPublisher.publishEvent(new UserCreatedEvent(usersPersistence.getId()));
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

    //POINTS
    @Transactional
    public void addPointsToUser(@NotNull Long userId, @NotNull Integer pointsToAdd) {
        if (pointsToAdd <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        }
        
        UsersPersistence user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UsersDomain domain = UsersDataMapper.toDomain(user);
        domain.addPoints(new PositiveOrZeroInteger(pointsToAdd));

        user.setCurrentPoints(domain.getCurrentPoints().value());
        user.setTotalPoints(domain.getTotalPoints().value());
        usersRepository.saveAndFlush(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void onNodeUnlocked(NodeUnlockedEvent event) {
        if (event.price() > 0) {
            subtractPointsToUser(event.userId(), event.price());
        }
    }

    @Transactional
    public void subtractPointsToUser(@NotNull Long userId, @NotNull Integer pointsToSubtract) {
        if (pointsToSubtract <= 0) {
            throw new IllegalArgumentException("Points to subtract must be positive");
        }
        
        UsersPersistence user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UsersDomain domain = UsersDataMapper.toDomain(user);
        domain.subtractCurrentPoints(new PositiveOrZeroInteger(pointsToSubtract));

        user.setCurrentPoints(domain.getCurrentPoints().value());
        user.setTotalPoints(domain.getTotalPoints().value());
        usersRepository.saveAndFlush(user);
    }
}
