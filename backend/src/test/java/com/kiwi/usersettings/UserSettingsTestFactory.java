package com.kiwi.usersettings;

public class UserSettingsTestFactory {
    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                true,
                UserSettingsEnums.Theme.LIGHT
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                "jake@thedog.com",
                false,
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