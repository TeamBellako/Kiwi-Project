package com.kiwi.settings;

import com.kiwi.common.RegexUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Optional<SettingsDTO> getSettingsByEmail(String email) {
        if (!RegexUtils.isValidEmail(email)) throw new SettingsInvalidException("Invalid email format");

        return Optional.ofNullable(settingsRepository.findByEmail(email)
                .map(Settings::toDTO)
                .orElseThrow(() -> new SettingsNotFoundException(email)));
    }

    @Transactional
    public SettingsDTO updateSettings(@Valid SettingsDTO settingsDTO) {
        Settings settings = settingsDTO.toDomainObject();
        
        Optional<Settings> existing = settingsRepository.findByEmail(settings.getEmail().value());
        if (existing.isEmpty()) throw new SettingsNotFoundException(settings.getEmail().value());
        
        Settings existingSettings = existing.get();
        existingSettings.mergeFromDTO(settingsDTO);

        return settingsRepository.saveAndFlush(existingSettings).toDTO();
    }
}
