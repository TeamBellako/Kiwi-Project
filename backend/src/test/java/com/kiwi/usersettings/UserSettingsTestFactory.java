package com.kiwi.usersettings;

public class UserSettingsTestFactory {

    public static UserSettingsDTO validUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                70,                       
                80,                       
                true                      
        );
    }

    public static UserSettingsDTO updatedUserSettingsDTO() {
        return new UserSettingsDTO(
                "finn@thehuman.com",
                50,                       
                40,                       
                false                     
        );
    }

    public static UserSettingsDTO invalidUserSettingsDTO() {
        return new UserSettingsDTO(
                "bmolovesfootball.com",   
                150,                      
                -10,                      
                true                      
        );
    }
}
