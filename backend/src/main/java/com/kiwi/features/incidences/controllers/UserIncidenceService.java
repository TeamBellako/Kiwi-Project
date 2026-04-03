package com.kiwi.features.incidences.controllers;

import com.kiwi.features.incidences.data.IncidencePersistence;
import com.kiwi.features.incidences.data.UserIncidenceDTO;
import com.kiwi.features.incidences.data.UserIncidenceKey;
import com.kiwi.features.incidences.data.UserIncidencePersistence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserIncidenceService {
    
    private final IncidenceRepository incidenceRepository;
    private final UserIncidenceRepository userIncidenceRepository;

    public UserIncidenceService(IncidenceRepository incidenceRepository, UserIncidenceRepository userIncidenceRepository) {
        this.incidenceRepository = incidenceRepository;
        this.userIncidenceRepository = userIncidenceRepository;
    }

    @Transactional
    public void updateOrCreateUserIncidence(Long userId, UserIncidenceDTO dto) {
        IncidencePersistence incidence = incidenceRepository.findByName(dto.getName())
                .orElseGet(() -> {
                    IncidencePersistence newIncidence = new IncidencePersistence();
                    newIncidence.setName(dto.getName());
                    return incidenceRepository.save(newIncidence);
                });

        UserIncidencePersistence userIncidence = userIncidenceRepository
                .findByIdUserIdAndIdIncidenceId(userId, incidence.getId())
                .orElseGet(() -> {
                    UserIncidencePersistence newUserIncidence = new UserIncidencePersistence();
                    newUserIncidence.setId(new UserIncidenceKey(userId, incidence.getId()));
                    return newUserIncidence;
                });

        userIncidence.setValue(dto.isValue());
        userIncidenceRepository.save(userIncidence);
    }
    
    public boolean getUserIncidence(Long userId, String incidenceName) {
        Optional<IncidencePersistence> incidencePersistence = incidenceRepository.findByName(incidenceName);
        if (incidencePersistence.isEmpty()) return false;
        
        Optional<UserIncidencePersistence> userIncidencePersistence = 
                userIncidenceRepository.findByIdUserIdAndIdIncidenceId(userId, incidencePersistence.get().getId());
        return userIncidencePersistence.map(UserIncidencePersistence::isValue).orElse(false);
    }
}
