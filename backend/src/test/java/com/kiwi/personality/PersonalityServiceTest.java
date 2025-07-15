package com.kiwi.personality;

import com.kiwi.features.personality.*;
import com.kiwi.features.users.UsersRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.personality.PersonalityTestFactory.*;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersonalityServiceTest {

    private final PersonalityRepository personalityRepository = Mockito.mock(PersonalityRepository.class);
    private final UsersRepository usersRepository = Mockito.mock(UsersRepository.class);
    private final PersonalityService personalityService = new PersonalityService(personalityRepository, usersRepository);

    @Test
    public void getPersonality_valid() {
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(validPersonality()));

        Personality personality = personalityService.getPersonality(validUserDTO().getEmail());
        assertNotNull(personality);
        assertEquals(validPersonality(), personality);
        verify(personalityRepository, Mockito.times(1)).findByUserEmail(validUserDTO().getEmail());
    }

    @Test(expected = PersonalityNotFoundException.class)
    public void getPersonality_invalid() {
        personalityService.getPersonality(validUserDTO().getEmail());
    }

    @Test
    public void updateRealName() {
        when(personalityRepository.saveAndFlush(validPersonality())).thenReturn(validPersonality());
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(validPersonality()));

        personalityService.updateRealName(validUserDTO().getEmail(), userNameRealDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(validPersonality());
    }

    @Test
    public void updateKnightName() {
        when(personalityRepository.saveAndFlush(validPersonality())).thenReturn(validPersonality());
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(validPersonality()));

        personalityService.updateKnightName(validUserDTO().getEmail(), userNameKnightDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(validPersonality());
    }

    @Test
    public void updateBuild() {
        when(personalityRepository.saveAndFlush(validPersonality())).thenReturn(validPersonality());
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(validPersonality()));

        personalityService.updateBuild(validUserDTO().getEmail(), buildDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(validPersonality());
    }

}
