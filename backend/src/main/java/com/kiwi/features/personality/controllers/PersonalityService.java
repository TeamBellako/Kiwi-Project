package com.kiwi.features.personality.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.personality.data.BuildType;
import com.kiwi.features.personality.exceptions.PersonalityNotFoundException;
import com.kiwi.features.personality.data.*;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import com.kiwi.features.users.data.UsersPersistence;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonalityService {

    private static final Logger log = LoggerFactory.getLogger(PersonalityService.class);

    private final PersonalityRepository personalityRepository;
    private final UsersService usersService;
    private final BuildInitializationService buildInitializationService;

    @Autowired
    public PersonalityService(
            PersonalityRepository personalityRepository,
            UsersService usersService,
            BuildInitializationService buildInitializationService
    ) {
        this.personalityRepository = personalityRepository;
        this.usersService = usersService;
        this.buildInitializationService = buildInitializationService;
    }

    @Transactional
    public PersonalityPersistence getPersonality(String email) {
        Optional<PersonalityPersistence> personalityPersistence = personalityRepository.findByUserEmail(new Email(email).value());
        if (personalityPersistence.isPresent()) { return personalityPersistence.get(); }
        throw new PersonalityNotFoundException(email);
    }

    @Transactional
    public PersonalityDomain getOrCreatePersonality(String email) {
        Optional<PersonalityPersistence> personalityPersistence = personalityRepository.findByUserEmail(email);
        return personalityPersistence.map(PersonalityDataMapper::toDomain).orElseGet(PersonalityDomain::new);
    }

    @Transactional
    public PersonalityPersistence saveToPersistence(String email, PersonalityDomain personalityDomain) {
        Optional<UsersPersistence> user = usersService.getUserByEmail(new Email(email));
        if (user.isPresent()) {
            Optional<PersonalityPersistence> personalityPersistence = personalityRepository.findByUserEmail(email);
            if (personalityPersistence.isPresent()) {
                PersonalityDataMapper.updatePersistence(personalityPersistence.get(), personalityDomain);
                return personalityRepository.saveAndFlush(personalityPersistence.get());
            }
            return personalityRepository.saveAndFlush(PersonalityDataMapper.toPersistence(user.get(), personalityDomain));
        } else {
            throw new UsersNotFoundException(email);
        }
    }

    @Transactional
    public PersonalityDTO updateRealName(String email, @Valid UserNameDTO userNameDTO) {
        PersonalityDomain personalityDomain = getOrCreatePersonality(email);
        personalityDomain.setRealName(userNameDTO.getName());
        return PersonalityDataMapper.toDTO(saveToPersistence(email, personalityDomain));
    }

    @Transactional
    public PersonalityDTO updateKnightName(String email, @Valid UserNameDTO userNameDTO) {
        PersonalityDomain personalityDomain = getOrCreatePersonality(email);
        personalityDomain.setKnightName(userNameDTO.getName());
        return PersonalityDataMapper.toDTO(saveToPersistence(email, personalityDomain));
    }

    @Transactional
    public PersonalityDTO updateBuild(String email, @Valid BuildDTO buildDTO) {
        PersonalityDomain personalityDomain = getOrCreatePersonality(email);
        personalityDomain.setBuild(buildDTO.getBuild());
        PersonalityPersistence saved = saveToPersistence(email, personalityDomain);

        BuildType.fromString(buildDTO.getBuild()).ifPresentOrElse(
                buildType -> {
                    Long userId = saved.getUser().getId();
                    buildInitializationService.initializeIfAbsent(userId, buildType);
                },
                () -> log.warn("Unknown build type '{}' — skipping combat initialization", buildDTO.getBuild())
        );

        return PersonalityDataMapper.toDTO(saved);
    }

    @Transactional
    public PersonalityDTO updateApps(String email, @Valid AppsDTO appsDTO) {
        PersonalityDomain personalityDomain = getOrCreatePersonality(email);
        personalityDomain.setGoodApps(appsDTO.getGoodApps());
        personalityDomain.setBadApps(appsDTO.getBadApps());
        personalityDomain.setNeutralApps(appsDTO.getNeutralApps());
        return PersonalityDataMapper.toDTO(saveToPersistence(email, personalityDomain));
    }

}
