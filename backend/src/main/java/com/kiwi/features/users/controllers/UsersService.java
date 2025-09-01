package com.kiwi.features.users.controllers;

import com.kiwi.features.users.data.*;
import com.kiwi.features.users.exceptions.UsersConflictException;
import com.kiwi.common.types.Email;
import com.kiwi.features.users.exceptions.UsersInvalidException;
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
            throw new UsersInvalidException(e.getMessage());
        }

        String email = userDomain.getEmail().value();
        if (usersRepository.existsByEmail(email)) {
            throw new UsersConflictException(email);
        }

        String hashedPassword = passwordEncoder.encode(userDomain.getPassword().value());
        UsersPersistence usersPersistence = UsersDataMapper.toPersistence(userDomain, hashedPassword);
        usersRepository.saveAndFlush(usersPersistence);
    }

    public Optional<UsersPersistence> getUserByEmail(@NotNull Email email) {
        return usersRepository.findByEmail(email.value());
    }
}
