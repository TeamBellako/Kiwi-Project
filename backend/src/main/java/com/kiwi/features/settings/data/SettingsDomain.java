package com.kiwi.features.settings.data;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @EqualsAndHashCode @ToString
public class SettingsDomain {
    private float soundVolume;
    private float musicVolume;

    public void update(SettingsDTO dto) {
        setSoundVolume(dto.getSoundVolume());
        setMusicVolume(dto.getMusicVolume());
    }
}
