package com.kiwi.users;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (this.usersRepository.existsByEmail(userEmailValue)) throw new UsersConflictException(userEmailValue);
        
        this.usersRepository.saveAndFlush(userDTO.toPersistenceObject());
    }
    
    public UsersDTO getUserByEmail(@NotNull Email email) {
        return null;
    }

    @Transactional
    public void updateUser(@Valid @NotNull UsersDTO userDTO) {
        
    }

    @Transactional
    public void deleteUser(@NotNull Email email) {
        
    }
}
