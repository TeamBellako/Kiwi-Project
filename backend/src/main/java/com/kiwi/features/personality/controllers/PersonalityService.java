package com.kiwi.features.personality.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.personality.exceptions.PersonalityNotFoundException;
import com.kiwi.features.personality.data.*;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import com.kiwi.features.users.data.UsersPersistence;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonalityService {
    private final PersonalityRepository personalityRepository;
    private final UsersService usersService;

    @Autowired
    public PersonalityService(PersonalityRepository personalityRepository, UsersService usersService) {
        this.personalityRepository = personalityRepository;
        this.usersService = usersService;
    }


    @Transactional
    public PersonalityPersistence getPersonality(String email) {
        Optional<PersonalityPersistence> personalityPersistence = personalityRepository.findByUserEmail(email);
        if (personalityPersistence.isPresent()) { return personalityPersistence.get(); }
        throw new PersonalityNotFoundException(email);
    }

    @Transactional
    public PersonalityPersistence getOrCreatePersonality(String email) {
        Optional<PersonalityPersistence> personalityPersistence = personalityRepository.findByUserEmail(email);
        if (personalityPersistence.isPresent()) {
            return personalityPersistence.get();
        } else {
            PersonalityPersistence newPersonalityPersistence = new PersonalityPersistence();
            Optional<UsersPersistence> user = usersService.getUserByEmail(new Email(email));
            if (user.isPresent()) {
                newPersonalityPersistence.setUser(user.get());
                return newPersonalityPersistence;
            } else {
                throw new UsersNotFoundException(email);
            }
        }
    }

    @Transactional
    public PersonalityDTO updateRealName(String email, @Valid UserNameDTO userNameDTO) {
        PersonalityPersistence newPersonalityPersistence = getOrCreatePersonality(email);
        newPersonalityPersistence.setRealName(userNameDTO.getName());
        PersonalityPersistence savedPersonalityPersistence = personalityRepository.saveAndFlush(newPersonalityPersistence);
        return PersonalityDataMapper.toDTO(savedPersonalityPersistence);
    }

    @Transactional
    public PersonalityDTO updateKnightName(String email, @Valid UserNameDTO userNameDTO) {
        PersonalityPersistence newPersonalityPersistence = getOrCreatePersonality(email);
        newPersonalityPersistence.setKnightName(userNameDTO.getName());
        PersonalityPersistence savedPersonalityPersistence = personalityRepository.saveAndFlush(newPersonalityPersistence);
        return PersonalityDataMapper.toDTO(savedPersonalityPersistence);
    }

    @Transactional
    public PersonalityDTO updateBuild(String email, @Valid BuildDTO buildDTO) {
        PersonalityPersistence newPersonalityPersistence = getOrCreatePersonality(email);
        newPersonalityPersistence.setBuild(buildDTO.getBuild());
        PersonalityPersistence savedPersonalityPersistence = personalityRepository.saveAndFlush(newPersonalityPersistence);
        return PersonalityDataMapper.toDTO(savedPersonalityPersistence);
    }

    @Transactional
    public PersonalityDTO updateApps(String email, @Valid AppsDTO appsDTO) {
        PersonalityPersistence newPersonalityPersistence = getOrCreatePersonality(email);
        newPersonalityPersistence.setGoodApps(appsDTO.getGoodApps());
        newPersonalityPersistence.setBadApps(appsDTO.getBadApps());
        PersonalityPersistence savedPersonalityPersistence = personalityRepository.saveAndFlush(newPersonalityPersistence);
        return PersonalityDataMapper.toDTO(savedPersonalityPersistence);
    }

}
