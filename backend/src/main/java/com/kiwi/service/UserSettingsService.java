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
        return null;
    }

    public Optional<UserSettings> getUserSettingsById(Integer id) {
        return null;
    }

    public UserSettings updateUserSettings(UserSettings userSettings) {
        return null;
    }

    public void deleteUserSettings(Integer id) {
    }
}
