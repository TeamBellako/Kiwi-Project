package com.kiwi.settings;

public class SettingsTestFactory {

    public static SettingsDTO validSettingsDTO() {
        return new SettingsDTO(
                "finn@thehuman.com",
                70,                       
                80
        );
    }

    public static SettingsDTO updatedSettingsDTO() {
        return new SettingsDTO(
                "finn@thehuman.com",
                50,                       
                40
        );
    }

    public static SettingsDTO invalidSettingsDTO() {
        return new SettingsDTO(
                "bmolovesfootball.com",   
                150,                      
                -10                     
        );
    }
}
