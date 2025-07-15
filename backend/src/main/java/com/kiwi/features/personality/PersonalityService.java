package com.kiwi.features.personality;

import com.kiwi.features.users.UsersNotFoundException;
import com.kiwi.features.users.UsersPersistence;
import com.kiwi.features.users.UsersRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonalityService {
    private final PersonalityRepository personalityRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public PersonalityService(PersonalityRepository personalityRepository, UsersRepository usersRepository) {
        this.personalityRepository = personalityRepository;
        this.usersRepository = usersRepository;
    }


    @Transactional
    public Personality getPersonality(String email) {
        Optional<Personality> personality = personalityRepository.findByUserEmail(email);
        if (personality.isPresent()) { return personality.get(); }
        throw new PersonalityNotFoundException(email);
    }

    @Transactional
    public Personality getOrCreatePersonality(String email) {
        Optional<Personality> personality = personalityRepository.findByUserEmail(email);
        if (personality.isPresent()) {
            return personality.get();
        } else {
            Personality newPersonality = new Personality("", "", "");
            Optional<UsersPersistence> user = usersRepository.findByEmail(email);
            if (user.isPresent()) {
                newPersonality.setUser(user.get());
                return newPersonality;
            } else {
                throw new UsersNotFoundException(email);
            }
        }
    }

    @Transactional
    public PersonalityDTO updateRealName(String email, @Valid UserNameDTO userNameDTO) {
        Personality newPersonality = getOrCreatePersonality(email);
        newPersonality.setRealName(userNameDTO.getName());
        return personalityRepository.saveAndFlush(newPersonality).toDTO();
    }

    @Transactional
    public PersonalityDTO updateKnightName(String email, @Valid UserNameDTO userNameDTO) {
        Personality newPersonality = getOrCreatePersonality(email);
        newPersonality.setKnightName(userNameDTO.getName());
        return personalityRepository.saveAndFlush(newPersonality).toDTO();
    }

    @Transactional
    public PersonalityDTO updateBuild(String email, @Valid BuildDTO buildDTO) {
        Personality newPersonality = getOrCreatePersonality(email);
        newPersonality.setBuild(buildDTO.getBuild());
        return personalityRepository.saveAndFlush(newPersonality).toDTO();
    }

}
