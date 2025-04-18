package com.kiwi.usersettings;

import jakarta.validation.Valid;
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
    public UserSettings createUserSettings(@Valid UserSettings userSettings) {
        if (userSettings.getId() != null && userSettingsRepository.existsById(userSettings.getId())) throw new UserSettingsConflictException(userSettings.getId());
        userSettings.validate();

        UserSettings savedUserSettings;
        try {
            savedUserSettings = userSettingsRepository.save(userSettings);
            if (savedUserSettings == null) {
                throw new IllegalStateException("Failed to save UserSettings: returned invalid data");
            }
        } catch (Exception e) {
            throw new RuntimeException("Database error during save operation", e);
        }
        
        return savedUserSettings;
    }
    
    public Optional<UserSettings> getUserSettingsById(Integer id) {
        validateInputUserSettingsId(id);
        
        return Optional.ofNullable(userSettingsRepository.findById(id)
                .orElseThrow(() -> new UserSettingsNotFoundException(id)));
    }

    @Transactional
    public UserSettings updateUserSettings(@Valid UserSettings userSettings) {
        if (userSettings == null) throw new IllegalArgumentException();
        userSettings.validate();
        if (!userSettingsRepository.existsById(userSettings.getId())) throw new UserSettingsNotFoundException(userSettings.getId());
        
        return userSettingsRepository.save(userSettings);
    }

    @Transactional
    public void deleteUserSettings(Integer id) {
        validateInputUserSettingsId(id);
        if (!userSettingsRepository.existsById(id)) throw new UserSettingsNotFoundException(id);
        
        userSettingsRepository.deleteById(id);
    }
    
    private void validateInputUserSettingsId(Integer id) {
        if (id <= 0) throw new IllegalArgumentException("UserSettings' ids are always bigger than zero");
    }
}