package com.kiwi.personality;

import com.kiwi.common.types.Email;
import com.kiwi.features.personality.controllers.PersonalityRepository;
import com.kiwi.features.personality.controllers.PersonalityService;
import com.kiwi.features.personality.data.PersonalityDataMapper;
import com.kiwi.features.personality.data.PersonalityPersistence;
import com.kiwi.features.personality.exceptions.PersonalityNotFoundException;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersPersistence;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.kiwi.personality.PersonalityTestFactory.*;
import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PersonalityServiceTests {

    private final PersonalityRepository personalityRepository = Mockito.mock(PersonalityRepository.class);
    private final UsersService usersService = Mockito.mock(UsersService.class);
    private final PersonalityService personalityService = new PersonalityService(personalityRepository, usersService);

    @Test
    public void getPersonality_valid() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(personalityPersistence));

        PersonalityPersistence newPersonalityPersistence = personalityService.getPersonality(validUserDTO().getEmail());
        assertNotNull(newPersonalityPersistence);
        assertEquals(newPersonalityPersistence, personalityPersistence);
        verify(personalityRepository, Mockito.times(1)).findByUserEmail(validUserDTO().getEmail());
    }

    @Test(expected = PersonalityNotFoundException.class)
    public void getPersonality_notFound() {
        personalityService.getPersonality(validUserDTO().getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSettings_invalidInput_throwsIllegalArgumentException() {
        personalityService.getPersonality(invalidUserDTO().getEmail());
    }

    @Test
    public void updateRealName() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        when(personalityRepository.saveAndFlush(personalityPersistence)).thenReturn(personalityPersistence);
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(personalityPersistence));

        personalityService.updateRealName(validUserDTO().getEmail(), userNameRealDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(personalityPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateRealName_nullInput_throwsNullPointerException() {
        when(usersService.getUserByEmail(new Email(validUserDTO().getEmail())))
                .thenReturn(Optional.of(UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword())));

        personalityService.updateRealName(validUserDTO().getEmail(), null);
    }

    @Test
    public void updateKnightName() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        when(personalityRepository.saveAndFlush(personalityPersistence)).thenReturn(personalityPersistence);
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(personalityPersistence));

        personalityService.updateKnightName(validUserDTO().getEmail(), userNameKnightDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(personalityPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateKnightName_nullInput_throwsNullPointerException() {
        when(usersService.getUserByEmail(new Email(validUserDTO().getEmail())))
                .thenReturn(Optional.of(UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword())));

        personalityService.updateKnightName(validUserDTO().getEmail(), null);
    }

    @Test
    public void updateBuild() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        when(personalityRepository.saveAndFlush(personalityPersistence)).thenReturn(personalityPersistence);
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(personalityPersistence));

        personalityService.updateBuild(validUserDTO().getEmail(), buildDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(personalityPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateBuild_nullInput_throwsNullPointerException() {
        when(usersService.getUserByEmail(new Email(validUserDTO().getEmail())))
                .thenReturn(Optional.of(UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword())));

        personalityService.updateBuild(validUserDTO().getEmail(), null);
    }

    @Test
    public void updateApps() {
        UsersPersistence savedUser = usersService.getUserByEmail(new Email(validUserDTO().getEmail())).orElse(null);
        PersonalityPersistence personalityPersistence = PersonalityDataMapper.toPersistence(savedUser, personalityDTO());
        when(personalityRepository.saveAndFlush(personalityPersistence)).thenReturn(personalityPersistence);
        when(personalityRepository.findByUserEmail(validUserDTO().getEmail())).thenReturn(Optional.of(personalityPersistence));

        personalityService.updateApps(validUserDTO().getEmail(), appsDTO());
        verify(personalityRepository, Mockito.times(1)).saveAndFlush(personalityPersistence);
    }

    @Test(expected = NullPointerException.class)
    public void updateApps_nullInput_throwsNullPointerException() {
        when(usersService.getUserByEmail(new Email(validUserDTO().getEmail())))
                .thenReturn(Optional.of(UsersDataMapper.toPersistence(validUserDTO(), validUserDTO().getPassword())));

        personalityService.updateApps(validUserDTO().getEmail(), null);
    }

}
