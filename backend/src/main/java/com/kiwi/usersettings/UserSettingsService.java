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

    public Optional<UserSettingsDTO> getUserSettingsByEmail(String email) {
        if (!RegexUtils.isValidEmail(email)) throw new UserSettingsInvalidException("Invalid email format");

        return Optional.ofNullable(userSettingsRepository.findByEmail(email)
                .map(UserSettings::toDTO)
                .orElseThrow(() -> new UserSettingsNotFoundException(email)));
    }

    @Transactional
    public UserSettingsDTO updateUserSettings(@Valid UserSettingsDTO userSettingsDTO) {
        UserSettings userSettings = userSettingsDTO.toDomainObject();
        if (userSettings == null) throw new IllegalArgumentException("Invalid user settings provided");
        
        Optional<UserSettings> existing = userSettingsRepository.findByEmail(userSettings.getEmail());
        if (existing.isEmpty()) throw new UserSettingsNotFoundException(userSettings.getEmail());
        
        UserSettings existingUserSettings = existing.get();
        existingUserSettings.mergeFromDTO(userSettingsDTO);

        return userSettingsRepository.saveAndFlush(existingUserSettings).toDTO();
    }
}
