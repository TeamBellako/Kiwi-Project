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

    public static UserSettings duplicateUserSettings() {
        return new UserSettings(
                "finnthehuman@gmail.com",
                false,
                UserSettings.Theme.DARK
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

    public static UserSettings noIdUserSettings() {
        return new UserSettings(
                "princessbugglegum@gmail.com",
                false,
                UserSettings.Theme.LIGHT
        );
    }

    public static UserSettings invalidUserSettings() {
        return new UserSettings(
                -1,
                "bmolovesfootball",
                false,
                UserSettings.Theme.DARK
        );
    }
    
    public static UserSettings invalidNoIdUserSettings() {
        return new UserSettings(
                "marcelineandsimon4ever",
                false,
                UserSettings.Theme.DARK
        );
    }
    
}