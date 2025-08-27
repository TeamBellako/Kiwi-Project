package com.kiwi.personality;

import com.kiwi.features.personality.data.PersonalityDataMapper;
import com.kiwi.features.personality.data.PersonalityPersistence;
import com.kiwi.features.personality.controllers.PersonalityRepository;
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

import static com.kiwi.personality.PersonalityTestFactory.personalityDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
public class PersonalityRepositoryTests {
    
    @Autowired
    private PersonalityRepository personalityRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void findPersonality() {
        UsersDomain user = UsersDataMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersDataMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).orElse(null);

        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        personalityPersistence.setUser(savedUser);

        personalityRepository.saveAndFlush(personalityPersistence);
        Optional<PersonalityPersistence> savedPersonality = personalityRepository.findByUserEmail(validUserDTO().getEmail());
        assertTrue(savedPersonality.isPresent());
    }

}