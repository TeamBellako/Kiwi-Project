package com.kiwi.users;

import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
public class UsersRepositoryTests {
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Test
    public void createValidUser() {
        UsersDomain userDomain = UsersDataMapper.toDomain(validUserDTO());
        String hashedPassword = validUserDTO().getPassword();
        UsersPersistence userPersistence = UsersDataMapper.toPersistence(userDomain, hashedPassword);
        usersRepository.saveAndFlush(userPersistence);
        assertTrue(usersRepository.findByEmail(validUserDTO().getEmail()).isPresent());
    }
    
    @Test
    public void getNonExistingUser() {
        assertEquals(Optional.empty(), usersRepository.findByEmail(validUserDTO().getEmail()));
    }
}
