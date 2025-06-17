package com.kiwi.users;

import com.kiwi.settings.SettingsRepository;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/DBTestSetUp.sql")
@ActiveProfiles("test")
public class UsersRepositoryTests {
    
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SettingsRepository settingsRepository;
    
    @Test
    public void createValidUser() {
        Users user =  UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();
        assertEquals(user, UsersMapper.toDomain(savedUser));
    }
    
    @Test
    public void getNonExistingUser() {
        assertEquals(Optional.empty(), usersRepository.findByEmail(validUserDTO().getEmail()));
    }
}
