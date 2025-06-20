package com.kiwi.users;

import com.kiwi.features.metrics.MetricsFactory;
import com.kiwi.features.users.UsersDTO;

import static com.kiwi.settings.SettingsTestFactory.invalidSettingsDTO;
import static com.kiwi.settings.SettingsTestFactory.validSettingsDTO;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO(
                "finn@thehuman.com",
                "Math3matical!",
                validSettingsDTO(),
                MetricsFactory.generateRandomMetricsSet(3, true)
        );
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(
                "bmolovesfootball",
                "kk",
                invalidSettingsDTO(),
                MetricsFactory.generateRandomMetricsSet(3, false)
        );
    }
}
