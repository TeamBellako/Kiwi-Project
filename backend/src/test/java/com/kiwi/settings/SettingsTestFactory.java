package com.kiwi.settings;

import com.kiwi.features.settings.data.SettingsDTO;

public class SettingsTestFactory {

    public static SettingsDTO settingsDTO() {
        return new SettingsDTO(.7f, .8f);
    }
}
