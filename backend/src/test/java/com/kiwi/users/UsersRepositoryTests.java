package com.kiwi.users;

import com.kiwi.usersettings.UserSettingsRepository;
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
import static com.kiwi.usersettings.UserSettingsTestFactory.validUserSettingsDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/UsersTestSetUp.sql")
@ActiveProfiles("test")
public class UsersRepositoryTests {
    
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    @Test
    public void createValidUser() {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());
        
        assertEquals(validUserDTO().toPersistenceObject(), usersRepository.findByEmail(validUserDTO().getEmail()).get());
    }
    
    @Test
    public void getNonExistingUser() {
        assertEquals(Optional.empty(), usersRepository.findByEmail(validUserDTO().getEmail()));
    }

    @Test
    public void updateValidUser() {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());

        UsersPersistence userUpdate = usersRepository.findByEmail(validUserDTO().getEmail()).get();
        userUpdate.setPassword(new Password("Marceline*Simon4Ever"));
        usersRepository.saveAndFlush(userUpdate);
        
        assertEquals(userUpdate, usersRepository.findByEmail(validUserDTO().getEmail()).get());
    }

    @Test
    public void deleteValidUserAlsoDeletesSettings() {
        usersRepository.saveAndFlush(validUserDTO().toPersistenceObject());
        
        assertEquals(validUserSettingsDTO().toDomainObject(), userSettingsRepository.findByEmail(validUserDTO().getEmail()).get());
        usersRepository.deleteByEmail(validUserDTO().getEmail());
        
        assertEquals(Optional.empty(), usersRepository.findByEmail(validUserDTO().getEmail()));
        assertEquals(Optional.empty(), userSettingsRepository.findByEmail(validUserDTO().getEmail()));
    }
}
