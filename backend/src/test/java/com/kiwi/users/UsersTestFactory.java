package com.kiwi.users;

import static com.kiwi.settings.SettingsTestFactory.invalidSettingsDTO;
import static com.kiwi.settings.SettingsTestFactory.validSettingsDTO;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO(
                "finn@thehuman.com",
                "Math3matical!",
                validSettingsDTO()
        );
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(
                "bmolovesfootball",
                "kk",
                invalidSettingsDTO()
        );
    }
}
