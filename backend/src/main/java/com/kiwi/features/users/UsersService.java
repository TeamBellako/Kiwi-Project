package com.kiwi.features.users;

import com.kiwi.types.Email;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public void createUser(@Valid @NotNull UsersDTO userDTO) {
        Users user = UsersMapper.toDomain(userDTO);
        
        String userEmailValue = user.getEmail().value(); 
        if (usersRepository.existsByEmail(userEmailValue)) throw new UsersConflictException(userEmailValue);
        
        String rawPassword = user.getPassword().value();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        UsersPersistence usersPersistence = new UsersPersistence(
                user.getEmail(),
                hashedPassword,
                user.getSettings()
        );
        usersRepository.saveAndFlush(usersPersistence);
    }

    public Optional<UsersPersistence> getUserByEmail(@NotNull Email email) {
        return usersRepository.findByEmail(email.value());
    }
}
