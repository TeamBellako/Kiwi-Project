package com.kiwi.personality;

import com.kiwi.features.personality.Personality;
import com.kiwi.features.personality.PersonalityRepository;
import com.kiwi.features.users.Users;
import com.kiwi.features.users.UsersMapper;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersRepository;
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

import static com.kiwi.personality.PersonalityTestFactory.validPersonality;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Sql(scripts = "/UsersTestSetUp.sql")
@ActiveProfiles("test")
public class PersonalityRepositoryTest {
    
    @Autowired
    private PersonalityRepository personalityRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void findPersonality() {
        Users user = UsersMapper.toDomain(validUserDTO());
        usersRepository.saveAndFlush(UsersMapper.toPersistence(user, validUserDTO().getPassword()));
        UsersPersistence savedUser = usersRepository.findByEmail(validUserDTO().getEmail()).get();

        Personality personality = validPersonality();
        personality.setUser(savedUser);

        personalityRepository.saveAndFlush(personality);
        Optional<Personality> savedPersonality = personalityRepository.findByUserEmail(validUserDTO().getEmail());
        assertTrue(savedPersonality.isPresent());
    }

}