package com.kiwi.features.settings.data;

import java.util.Objects;

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

    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float soundVolume) { this.soundVolume = soundVolume; }

    public float getMusicVolume() { return musicVolume; }
    public void setMusicVolume(float musicVolume) { this.musicVolume = musicVolume; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SettingsDomain other = (SettingsDomain) o;
        return Objects.equals(soundVolume, other.soundVolume) &&
                Objects.equals(musicVolume, other.musicVolume);
    }

}
