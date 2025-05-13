package com.kiwi.users;

import com.kiwi.exception.GlobalExceptionHandler;
import jdk.jshell.spi.ExecutionControl;
import org.junit.Test;
import org.springframework.context.annotation.Import;

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
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void getWithNullEmail() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void getNonExistingUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void updateValidUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void updateInvalidUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void updateNullUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void updateNonExistingUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void deleteValidUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void deleteNullUser() {
        // TODO
        assertEquals(true, false);
    }

    @Test
    public void deleteNonExistingUser() {
        // TODO
        assertEquals(true, false);
    }
}
