package com.kiwi.features.settings.data;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class SettingsDomain {
    private float soundVolume;
    private float musicVolume;

    public SettingsDomain() {
        this.soundVolume = 0;
        this.musicVolume = 0;
    }

    public SettingsDomain(float soundVolume, float musicVolume) {
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SettingsDomain other = (SettingsDomain) o;
        return Objects.equals(soundVolume, other.soundVolume) &&
                Objects.equals(musicVolume, other.musicVolume);
    }

}
