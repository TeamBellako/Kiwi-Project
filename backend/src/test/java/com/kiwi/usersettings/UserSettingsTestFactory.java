package com.kiwi.usersettings;

public class UserSettingsTestFactory {

    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                70,                       
                80
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                50,                       
                40
        );
    }

    public static UserSettingsDTO invalidUserSettingsDTO() {
        return new UserSettingsDTO(
                "bmolovesfootball.com",   
                150,                      
                -10                     
        );
    }
}
