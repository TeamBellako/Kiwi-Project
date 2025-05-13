package com.kiwi.users;

import com.kiwi.exception.GlobalExceptionHandler;
import org.junit.Test;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import({GlobalExceptionHandler.class})
public class UsersServiceTest {
    private final UsersRepositoryInMemory usersRepositoryInMemory = new UsersRepositoryInMemory();
    private final UsersService usersService = new UsersService(usersRepositoryInMemory);
    
    @Test
    public void createValidUser() {
        usersService.createUser(validUserDTO());
        
        assertEquals(validUserDTO().toPersistenceObject(), usersRepositoryInMemory.findByEmail(validUserDTO().getEmail()));
    }

    @Test(expected = IllegalArgumentException.class)
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
        usersRepositoryInMemory.saveAndFlush(validUserDTO().toPersistenceObject());
        
        assertEquals(validUserDTO(), usersService.getUserByEmail(new Email(validUserDTO().getEmail())).get());
    }

    @Test(expected = NullPointerException.class)
    public void getWithNullEmail() {
        usersService.getUserByEmail(null);
    }

    @Test
    public void getNonExistingUser() {
        assertEquals(Optional.empty(), usersService.getUserByEmail(new Email(validUserDTO().getEmail())));
    }

    @Test
    public void updateValidUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void updateInvalidUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void updateNullUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void updateNonExistingUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void deleteValidUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void deleteNullUser() {
        throw new AssertionError("Test not implemented yet");
    }

    @Test
    public void deleteNonExistingUser() {
        throw new AssertionError("Test not implemented yet");
    }
}
