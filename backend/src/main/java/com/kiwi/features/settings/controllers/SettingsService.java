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
        Optional<SettingsPersistence> settingsPersistence = settingsRepository.findByUserEmail(email);
        if (settingsPersistence.isPresent()) { return settingsPersistence.get(); }
        throw new SettingsNotFoundException(email);
    }

    @Transactional
    public SettingsPersistence getOrCreateSettings(String email) {
        Optional<SettingsPersistence> settingsPersistence = settingsRepository.findByUserEmail(email);
        if (settingsPersistence.isPresent()) {
            return settingsPersistence.get();
        } else {
            SettingsPersistence newSettingsPersistence = new SettingsPersistence();
            Optional<UsersPersistence> user = usersService.getUserByEmail(new Email(email));
            if (user.isPresent()) {
                newSettingsPersistence.setUser(user.get());
                return newSettingsPersistence;
            } else {
                throw new UsersNotFoundException(email);
            }
        }
    }

    @Transactional
    public SettingsDTO updateSettings(String email, @Valid SettingsDTO settingsDTO) {
        UsersPersistence userPersistence = getTargetUserPersistence(new Email(email));
        SettingsPersistence newSettingsPersistence = getOrCreateSettings(email);

        SettingsDomain updateSettingsDomain = SettingsDataMapper.toDomain(newSettingsPersistence);
        updateSettingsDomain.update(settingsDTO);
        SettingsPersistence updateSettingsPersistence = SettingsDataMapper.toPersistence(userPersistence, updateSettingsDomain);

        SettingsPersistence savedSettings = settingsRepository.saveAndFlush(updateSettingsPersistence);
        return SettingsDataMapper.toDTO(savedSettings);
    }

    private UsersPersistence getTargetUserPersistence(Email email) {
        Optional<UsersPersistence> targetUserPersistence = usersService.getUserByEmail(email);
        if (targetUserPersistence.isEmpty()) throw new UsersNotFoundException(email.value());
        return targetUserPersistence.get();
    }
}
