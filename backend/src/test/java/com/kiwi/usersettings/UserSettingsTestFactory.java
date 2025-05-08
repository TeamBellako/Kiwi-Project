package com.kiwi.usersettings;

public class UserSettingsTestFactory {
    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "finn@thehuman.com",
                true,
                UserSettingsEnums.Theme.LIGHT
        );
    }

    public static UserSettingsDTO duplicateUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "finn@thehuman.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "jake@thedog.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettingsDTO invalidUserSettingsDTO() {
        return new UserSettingsDTO(
                -1,
                "bmolovesfootball.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettings noIdUserSettings() {
        return new UserSettings(
                "princess@bugglegum.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }
    
    public static UserSettings invalidNoIdUserSettings() {
        return new UserSettings(
                "marcelineandsimon4ever.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }
}