package com.kiwi.usersettings;

public class UserSettingsTestFactory {
    public static UserSettings validUserSettings() {
        return new UserSettings(
                1,
                "finnthehuman@gmail.com",
                true,
                UserSettings.Theme.LIGHT
        );
    }

    public static UserSettings updatedUserSettings() {
        return new UserSettings(
                1,
                "jakethedog@gmail.com",
                false,
                UserSettings.Theme.DARK
        );
    }

    public static UserSettings invalidUserSettings() {
        return new UserSettings(
                -1,
                "bmotherobot.com",
                false,
                UserSettings.Theme.DARK
        );
    }
}