package com.kiwi.users;

import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.exceptions.CreateUserConflictException;
import com.kiwi.features.users.exceptions.CreateUserInvalidException;
import com.kiwi.common.types.Email;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;


public class UsersServiceTests {
    private final UsersTestRepositoryInMemory usersTestRepositoryInMemory = new UsersTestRepositoryInMemory();
    private final UsersService usersService = new UsersService(usersTestRepositoryInMemory, new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return true;
        }
    },null);
    
    private final String validEmailString = validUserDTO().getEmail(); 
    
    @Test
    public void createValidUser() {
        usersService.createUser(validLoginDTO());
        assertTrue(usersTestRepositoryInMemory.findByEmail(validEmailString).isPresent());
    }

    @Test(expected = CreateUserInvalidException.class)
    public void createInvalidUser() {
        usersService.createUser(invalidLoginDTO());
    }

    @Test(expected = NullPointerException.class)
    public void createNullUser() {
        usersService.createUser(null);
    }
    
    @Test(expected = CreateUserConflictException.class)
    public void createDuplicatedUser() {
        usersService.createUser(validLoginDTO());
        usersService.createUser(validLoginDTO());
    }

    @Test
    public void getValidUser() {
        UsersDomain user = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        usersTestRepositoryInMemory.saveAndFlush(UsersDataMapper.toPersistence(user, validLoginDTO().getPassword()));
        assertTrue(usersTestRepositoryInMemory.findByEmail(getValidEmail().value()).isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void getWithNullEmail() {
        usersService.getUserByEmail(null);
    }

    @Test
    public void getNonExistingUser() {
        assertEquals(Optional.empty(), usersService.getUserByEmail(getValidEmail()));
    }

    private Email getValidEmail() { return new Email(validEmailString); }
}
