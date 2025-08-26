package com.kiwi.features.settings.data;

import com.kiwi.features.users.data.UsersPersistence;

public class SettingsDataMapper {

    public static SettingsDomain toDomain(SettingsDTO dto) {
        return (dto == null) ? null :
                new SettingsDomain(
                        dto.getSoundVolume(),
                        dto.getMusicVolume()
                );
    }

    public static SettingsDomain toDomain(SettingsPersistence persistence) {
        return (persistence == null) ? null :
                new SettingsDomain(
                        persistence.getSoundVolume(),
                        persistence.getMusicVolume()
                );
    }

    public static SettingsPersistence toPersistence(UsersPersistence usersPersistence, SettingsDomain domain) {
        return (domain == null) ? null :
                SettingsPersistence.builder()
                        .user(usersPersistence)
                        .soundVolume(domain.getSoundVolume())
                        .musicVolume(domain.getMusicVolume())
                        .build();
    }

    public static SettingsPersistence toPersistence(UsersPersistence usersPersistence, SettingsDTO dto) {
        return toPersistence(usersPersistence, toDomain(dto));
    }

    public static SettingsDTO toDTO(SettingsDomain domain) {
        if (domain == null) return null;
        return new SettingsDTO(
                domain.getSoundVolume(),
                domain.getMusicVolume()
        );
    }

    public static SettingsDTO toDTO(SettingsPersistence persistence) {
        return toDTO(toDomain(persistence));
    }
}
