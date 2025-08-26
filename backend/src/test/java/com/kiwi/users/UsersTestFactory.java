package com.kiwi.users;

import com.kiwi.features.metrics.tests.MetricsFactory;
import com.kiwi.features.users.data.UsersDTO;

import static com.kiwi.settings.SettingsTestFactory.invalidSettingsDTO;
import static com.kiwi.settings.SettingsTestFactory.validSettingsDTO;

public class UsersTestFactory {
    public static UsersDTO validUserDTO() {
        return new UsersDTO(
                "finn@thehuman.com",
                "Math3matic!",
                validSettingsDTO(),
                MetricsFactory.generateRandomMetricsSet(3, true)
        );
    }
    
    public static UsersDTO invalidUserDTO() {
        return new UsersDTO(
                "football",
                "kk",
                invalidSettingsDTO(),
                MetricsFactory.generateRandomMetricsSet(3, false)
        );
    }
}
