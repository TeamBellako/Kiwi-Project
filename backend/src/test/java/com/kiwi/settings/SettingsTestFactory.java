package com.kiwi.settings;

import com.kiwi.features.settings.SettingsDTO;

public class SettingsTestFactory {

    public static SettingsDTO validSettingsDTO() {
        return new SettingsDTO(
                "finn@thehuman.com",
                .7f,
                .8f
        );
    }

    public static SettingsDTO updatedSettingsDTO() {
        return new SettingsDTO(
                "finn@thehuman.com",
                .5f,
                .4f
        );
    }

    public static SettingsDTO invalidSettingsDTO() {
        return new SettingsDTO(
                "football",
                1.5f,
                -0.1f
        );
    }
}
