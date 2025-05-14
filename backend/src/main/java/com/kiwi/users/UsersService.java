package com.kiwi.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    
    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public void createUser(@Valid @NotNull UsersDTO userDTO) {
        Users user = userDTO.toDomainObject();
        
        String userEmailValue = user.getEmail().value(); 
        if (usersRepository.existsByEmail(userEmailValue)) throw new UsersConflictException(userEmailValue);
        
        usersRepository.saveAndFlush(userDTO.toPersistenceObject());
    }
    
    public Optional<UsersDTO> getUserByEmail(@NotNull Email email) {
        return usersRepository.findByEmail(email.value()).map(UsersPersistence::toDTO);
    }

    @Transactional
    public void updateUser(@Valid @NotNull UsersDTO userDTO) {
        Users userUpdate = userDTO.toDomainObject();
        Email userUpdateEmail = userUpdate.getEmail();
        
        Optional<UsersPersistence> existingUserPersistenceOptional = usersRepository.findByEmail(userUpdateEmail.value());
        if (existingUserPersistenceOptional.isEmpty()) throw new UsersNotFoundException(userUpdateEmail.value());
        
        UsersPersistence usersPersistence = existingUserPersistenceOptional.get();
        usersPersistence.mergeFromDomainObject(userUpdate);
        
        usersRepository.saveAndFlush(usersPersistence);
    }

    @Transactional
    public void deleteUser(@NotNull Email email) {
        if (!usersRepository.existsByEmail(email.value())) throw new UsersNotFoundException(email.value());
        
        usersRepository.deleteByEmail(email.value());
    }
}
