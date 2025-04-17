package com.kiwi.service;

import com.kiwi.entity.UserSettings;
import com.kiwi.exception.UserSettingsNotFoundException;
import com.kiwi.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    
    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    
    @Transactional
    public UserSettings createUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }
    
    public Optional<UserSettings> getUserSettingsById(Integer id) {
        return Optional.ofNullable(userSettingsRepository.findById(id)
                .orElseThrow(() -> new UserSettingsNotFoundException(id)));
    }

    @Transactional
    public UserSettings updateUserSettings(UserSettings userSettings) {
        validateUserSettingsExistence(userSettings.getId());
        
        return userSettingsRepository.save(userSettings);
    }

    @Transactional
    public void deleteUserSettings(Integer id) {
        validateUserSettingsExistence(id);
        
        userSettingsRepository.deleteById(id);
    }

    private void validateUserSettingsExistence(Integer id) {
        if (!userSettingsRepository.existsById(id)) {
            throw new UserSettingsNotFoundException(id);
        }
    }
}
