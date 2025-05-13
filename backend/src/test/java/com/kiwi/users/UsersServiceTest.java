package com.kiwi.users;

import org.junit.Test;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersServiceTest {
    private final UsersRepositoryInMemory usersRepositoryInMemory = new UsersRepositoryInMemory();
    private final UsersService usersService = new UsersService(usersRepositoryInMemory);
    
    @Test
    public void createValidUser() {
        usersService.createUser(validUserDTO());
        
        assertEquals(validUserDTO().toPersistenceObject(), usersRepositoryInMemory.findByEmail(validUserDTO().getEmail()));
    }

    @Test
    public void createInvalidUser() {

    }

    @Test
    public void createNullUser() {

    }
    
    @Test
    public void createDuplicatedUser() {

    }

    @Test
    public void getValidUser() {

    }

    @Test
    public void getWithNullEmail() {

    }

    @Test
    public void getNonExistingUser() {

    }

    @Test
    public void updateValidUser() {

    }

    @Test
    public void updateInvalidUser() {

    }

    @Test
    public void updateNullUser() {

    }

    @Test
    public void updateNonExistingUser() {

    }

    @Test
    public void deleteValidUser() {

    }

    @Test
    public void deleteNullUser() {

    }

    @Test
    public void deleteNonExistingUser() {

    }
    
}
