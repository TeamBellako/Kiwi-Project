package com.kiwi.usersettings;

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
        validateUserSettings(userSettings);
        
        if (userSettingsRepository.existsById(userSettings.getId())) {
            throw new UserSettingsConflictException(userSettings.getId());
        }

        UserSettings savedUserSettings;
        try {
            savedUserSettings = userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            throw new RuntimeException("Database error during save operation", e);
        }

        if (!savedUserSettings.isValid()) {
            throw new IllegalStateException("Failed to save UserSettings: returned invalid data");
        }
        
        return savedUserSettings;
    }
    
    public Optional<UserSettings> getUserSettingsById(Integer id) {
        validateUserSettingsId(id);
        
        return Optional.ofNullable(userSettingsRepository.findById(id)
                .orElseThrow(() -> new UserSettingsNotFoundException(id)));
    }

    @Transactional
    public UserSettings updateUserSettings(UserSettings userSettings) {
        validateUserSettings(userSettings);
        validateUserSettingsExistence(userSettings.getId());
        
        return userSettingsRepository.save(userSettings);
    }

    @Transactional
    public void deleteUserSettings(Integer id) {
        validateUserSettingsId(id);
        validateUserSettingsExistence(id);
        
        userSettingsRepository.deleteById(id);
    }
    
    private void validateUserSettingsId(Integer id) {
        if (id <= 0) throw new IllegalArgumentException("UserSettings' id must be bigger than zero");
    }
    
    private void validateUserSettings(UserSettings userSettings) {
        if (userSettings == null) throw new IllegalArgumentException();
        if (!userSettings.isValid()) throw new UserSettingsInvalidException();
    }

    private void validateUserSettingsExistence(Integer id) {
        if (!userSettingsRepository.existsById(id)) {
            throw new UserSettingsNotFoundException(id);
        }
    }
}
