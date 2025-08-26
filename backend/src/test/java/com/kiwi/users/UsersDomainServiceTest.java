package com.kiwi.users;

import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.exceptions.UsersConflictException;
import com.kiwi.features.users.exceptions.UsersInvalidException;
import com.kiwi.common.types.Email;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;

public class UsersDomainServiceTest {
    private final UsersRepositoryInMemory usersRepositoryInMemory = new UsersRepositoryInMemory();
    private final UsersService usersService = new UsersService(usersRepositoryInMemory, new PasswordEncoder() {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return true;
        }
    });
    
    private final String validEmailString = validUserDTO().getEmail(); 
    
    @Test
    public void createValidUser() {
        usersService.createUser(validUserDTO());
        assertTrue(usersRepositoryInMemory.findByEmail(validEmailString).isPresent());
    }

    @Test(expected = UsersInvalidException.class)
    public void createInvalidUser() {
        usersService.createUser(invalidUserDTO());
    }

    @Test(expected = NullPointerException.class)
    public void createNullUser() {
        usersService.createUser(null);
    }
    
    @Test(expected = UsersConflictException.class)
    public void createDuplicatedUser() {
        usersService.createUser(validUserDTO());
        usersService.createUser(validUserDTO());
    }

    @Test
    public void getValidUser() {
        UsersDomain user = UsersDataMapper.toDomain(validUserDTO());
        usersRepositoryInMemory.saveAndFlush(UsersDataMapper.toPersistence(user, validUserDTO().getPassword()));
        assertTrue(usersRepositoryInMemory.findByEmail(getValidEmail().value()).isPresent());
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
