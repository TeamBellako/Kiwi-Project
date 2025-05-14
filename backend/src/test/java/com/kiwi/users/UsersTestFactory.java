package com.kiwi.users;

import static com.kiwi.usersettings.UserSettingsTestFactory.invalidUserSettingsDTO;
import static com.kiwi.usersettings.UserSettingsTestFactory.validUserSettingsDTO;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO(
                "finn@thehuman.com",
                "Math3matical!",
                validUserSettingsDTO()
        );
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(
                "bmolovesfootball",
                "kk",
                invalidUserSettingsDTO()
        );
    }
}
