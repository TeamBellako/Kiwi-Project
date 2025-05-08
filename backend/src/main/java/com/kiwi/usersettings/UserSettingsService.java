package com.kiwi.usersettings;

import com.kiwi.utils.RegexUtils;
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
    public UserSettingsDTO createUserSettings(@Valid UserSettingsDTO userSettingsDTO) {
        UserSettings userSettings = userSettingsDTO.toDomainObject();
        if (userSettings == null) {
            throw new IllegalArgumentException("Invalid user settings provided");
        }

        if (userSettings.getId() != null && userSettingsRepository.existsById(userSettings.getId())) {
            throw new UserSettingsConflictException(userSettings.getId());
        }

        UserSettings savedUserSettings;
        try {
            savedUserSettings = userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            throw new RuntimeException("Database error during save operation", e);
        }

        return savedUserSettings.toDTO();
    }

    public Optional<UserSettingsDTO> getUserSettingsById(Integer id) {
        validateInputUserSettingsId(id);

        return Optional.ofNullable(userSettingsRepository.findById(id)
                .map(UserSettings::toDTO)
                .orElseThrow(() -> new UserSettingsNotFoundException(id)));
    }

    public Optional<UserSettingsDTO> getUserSettingsByEmail(String email) {
        if (!RegexUtils.isValidEmail(email)) throw new UserSettingsInvalidException("Invalid email format");

        return Optional.ofNullable(userSettingsRepository.findByEmail(email)
                .map(UserSettings::toDTO)
                .orElseThrow(() -> new UserSettingsNotFoundException(email)));
    }

    @Transactional
    public UserSettingsDTO updateUserSettings(@Valid UserSettingsDTO userSettingsDTO) {
        UserSettings userSettings = userSettingsDTO.toDomainObject();
        if (userSettings == null) {
            throw new IllegalArgumentException("Invalid user settings provided");
        }

        if (!userSettingsRepository.existsById(userSettings.getId())) {
            throw new UserSettingsNotFoundException(userSettings.getId());
        }

        UserSettings updatedUserSettings = userSettingsRepository.save(userSettings);
        return updatedUserSettings.toDTO();
    }

    @Transactional
    public void deleteUserSettings(Integer id) {
        validateInputUserSettingsId(id);

        if (!userSettingsRepository.existsById(id)) {
            throw new UserSettingsNotFoundException(id);
        }

        userSettingsRepository.deleteById(id);
    }

    private void validateInputUserSettingsId(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("UserSettings' ids are always greater than zero");
        }
    }
}
