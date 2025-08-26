package com.kiwi.features.settings.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.features.settings.data.Settings;
import com.kiwi.features.settings.data.SettingsDTO;
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
        new Email(email); // Throw exception if invalid email

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
