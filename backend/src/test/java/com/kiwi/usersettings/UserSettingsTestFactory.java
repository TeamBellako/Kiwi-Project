package com.kiwi.usersettings;

public class UserSettingsTestFactory {
    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "finnthehuman@gmail.com",
                true,
                UserSettingsEnums.Theme.LIGHT
        );
    }

    public static UserSettingsDTO duplicateUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "finnthehuman@gmail.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                1,
                "jakethedog@gmail.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettingsDTO invalidUserSettingsDTO() {
        return new UserSettingsDTO(
                -1,
                "bmolovesfootball",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }

    public static UserSettings noIdUserSettings() {
        return new UserSettings(
                "princessbugglegum@gmail.com",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }
    
    public static UserSettings invalidNoIdUserSettings() {
        return new UserSettings(
                "marcelineandsimon4ever",
                false,
                UserSettingsEnums.Theme.DARK
        );
    }
}