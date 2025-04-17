package com.kiwi.service;

import com.kiwi.entity.UserSettings;
import com.kiwi.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;

    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    public UserSettings createUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }

    public Optional<UserSettings> getUserSettingsById(Integer id) {
        return Optional.of(userSettingsRepository.findById(id))
                .orElseThrow(() -> new RuntimeException(String.format("UserSettings with id %d not found", id)));
    }

    public UserSettings updateUserSettings(UserSettings userSettings) {
        if (!userSettingsRepository.existsById(userSettings.getId())) throw new RuntimeException(String.format("UserSettings with id %d not found", userSettings.getId()));
        
        return userSettingsRepository.save(userSettings);
    }


    public void deleteUserSettings(Integer id) {
        if (!userSettingsRepository.existsById(id)) throw new RuntimeException(String.format("UserSettings with id %d not found", id));
        
        userSettingsRepository.deleteById(id);
    }
}
