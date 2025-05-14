package com.kiwi.usersettings;

public class UserSettingsTestFactory {
    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                false,
                UserSettingsEnums.Theme.LIGHT
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                true,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettingsDTO invalidUserSettingsDTO() {
        return new UserSettingsDTO(
                "bmolovesfootball.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }
}