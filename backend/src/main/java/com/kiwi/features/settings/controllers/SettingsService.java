package com.kiwi.features.settings.controllers;

import com.kiwi.common.types.Email;
import com.kiwi.features.settings.data.SettingsDataMapper;
import com.kiwi.features.settings.data.SettingsDomain;
import com.kiwi.features.settings.data.SettingsPersistence;
import com.kiwi.features.settings.exceptions.SettingsNotFoundException;
import com.kiwi.features.settings.data.SettingsDTO;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.features.users.exceptions.UsersNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final UsersService usersService;

    @Autowired
    public SettingsService(SettingsRepository settingsRepository, UsersService usersService) {
        this.settingsRepository = settingsRepository;
        this.usersService = usersService;
    }

    public SettingsPersistence getSettings(String email) {
        Optional<SettingsPersistence> settingsPersistence = settingsRepository.findByUserEmail(new Email(email).value());
        if (settingsPersistence.isPresent()) { return settingsPersistence.get(); }
        throw new SettingsNotFoundException(email);
    }

    @Transactional
    public SettingsDomain getOrCreateSettings(String email) {
        Optional<SettingsPersistence> settingsPersistence = settingsRepository.findByUserEmail(email);
        return settingsPersistence.map(SettingsDataMapper::toDomain).orElseGet(SettingsDomain::new);
    }

    @Transactional
    public SettingsPersistence saveToPersistence(String email, SettingsDomain settingsDomain) {
        Optional<UsersPersistence> user = usersService.getUserByEmail(new Email(email));
        if (user.isPresent()) {
            Optional<SettingsPersistence> settingsPersistence = settingsRepository.findByUserEmail(email);
            if (settingsPersistence.isPresent()) {
                SettingsDataMapper.updatePersistence(settingsPersistence.get(), settingsDomain);
                return settingsRepository.saveAndFlush(settingsPersistence.get());
            }
            return settingsRepository.saveAndFlush(SettingsDataMapper.toPersistence(user.get(), settingsDomain));
        } else {
            throw new UsersNotFoundException(email);
        }
    }

    @Transactional
    public SettingsDTO updateSettings(String email, @Valid SettingsDTO settingsDTO) {
        SettingsDomain updateSettingsDomain = getOrCreateSettings(email);
        SettingsDataMapper.updateDomain(updateSettingsDomain, settingsDTO);
        return SettingsDataMapper.toDTO(saveToPersistence(email, updateSettingsDomain));
    }
}
